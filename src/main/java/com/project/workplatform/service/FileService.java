package com.project.workplatform.service;

import com.project.workplatform.dao.FileApplyMapper;
import com.project.workplatform.dao.FileMapper;
import com.project.workplatform.data.request.file.DealFileApplyRequest;
import com.project.workplatform.data.request.file.FileApplyRequest;
import com.project.workplatform.exception.CustomException;
import com.project.workplatform.exception.CustomExceptionType;
import com.project.workplatform.exception.ExceptionMessage;
import com.project.workplatform.pojo.FileApply;
import com.project.workplatform.pojo.UploadFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

/**
 * @author zengjingran
 */
@Service
@Transactional(rollbackFor = RuntimeException.class)
public class FileService {

    @Autowired
    private FileMapper mapper;

    @Autowired
    private FileApplyMapper applyMapper;

    public void saveFile(MultipartFile file, Integer userId) {
        if (file.isEmpty()) {
            throw new CustomException(CustomExceptionType.NORMAL_ERROR, ExceptionMessage.FILE_EMPTY);
        }
        //获取上传文件原来的名称
        String filename = file.getOriginalFilename();
        String filePath = "/Users/temp/";
        File temp = new File(filePath);
        if (!temp.exists()) {
            temp.mkdirs();
        }
        String fileFullName = filePath + filename;
        File localFile = new File(fileFullName);
        try {
            //保存文件
            file.transferTo(localFile);
            System.out.println(file.getOriginalFilename() + " 上传成功");
            //记录MySQL
            UploadFile uploadFile = new UploadFile();
            uploadFile.setFileFullName(fileFullName);
            uploadFile.setUserId(userId);
            mapper.insertSelective(uploadFile);
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomException(CustomExceptionType.NORMAL_ERROR, ExceptionMessage.FILE_UPLOAD_FAILED);
        }
    }

    public void apply(FileApplyRequest fileApplyRequest, Integer userId) {
        FileApply fileApply = new FileApply();
        fileApply.setFileId(fileApplyRequest.getFileId());
        fileApply.setUserId(userId);
        applyMapper.insertSelective(fileApply);
    }

    public void dealApply(DealFileApplyRequest dealFileApplyRequest, Integer userId) {
        //更新Apply
        FileApply fileApply = applyMapper.selectByPrimaryKey(dealFileApplyRequest.getApplyId());
        fileApply.setTag(dealFileApplyRequest.isAgree() ? 1 : 2);
        applyMapper.updateByPrimaryKeySelective(fileApply);
        //更新File
        UploadFile uploadFile = mapper.selectByPrimaryKey(fileApply.getFileId());
        String readerId = uploadFile.getReaderId();
        readerId = StringUtils.hasLength(readerId) ? readerId + "," + fileApply.getUserId() : String.valueOf(fileApply.getUserId());
        uploadFile.setReaderId(readerId);
        mapper.updateByPrimaryKeySelective(uploadFile);
    }


    public void download(Integer fileId, Integer userId, HttpServletResponse response) {
        UploadFile uploadFile = mapper.selectByPrimaryKey(fileId);
        String readerId = uploadFile.getReaderId();
        String[] idList = readerId.split(",");
        //校验权限
        boolean canDownload = false;
        for (String item : idList) {
            if (item.equals(String.valueOf(userId))) {
                canDownload = true;
            }
        }
        if (!canDownload) {
            throw new CustomException(CustomExceptionType.PERMISSION_ERROR, ExceptionMessage.FILE_PERMISSION_DENIED);
        }
        String fileFullName = uploadFile.getFileFullName();
        //获取文件
        File file = new File(fileFullName);
        //获取文件名
        String filename = file.getName();
        //获取文件后缀名
        String ext = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

        // 将文件写入输入流
        try{
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStream fis = new BufferedInputStream(fileInputStream);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();

            //清空response
            response.reset();
            //设置response的Header
            response.setCharacterEncoding("UTF-8");
            //Content-Disposition的作用：告知浏览器以何种方式显示响应返回的文件，用浏览器打开还是以附件的形式下载到本地保存
            //attachment表示以附件方式下载   inline表示在线打开   "Content-Disposition: inline; filename=文件名.mp3"
            //filename表示文件的默认名称，因为网络传输只支持URL编码的相关支付，因此需要将文件名URL编码后进行传输,前端收到后需要反编码才能获取到真正的名称
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
            // 告知浏览器文件的大小
            response.addHeader("Content-Length", "" + file.length());
            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            outputStream.write(buffer);
            outputStream.flush();
        }catch (IOException ex){
            throw new CustomException(CustomExceptionType.NORMAL_ERROR,ExceptionMessage.FILE_DOWNLOAD_FAILED);
        }
    }
}
