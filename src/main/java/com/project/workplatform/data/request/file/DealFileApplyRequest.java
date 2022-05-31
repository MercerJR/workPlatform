package com.project.workplatform.data.request.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DealFileApplyRequest {

    private Integer applyId;

    private boolean agree;

}
