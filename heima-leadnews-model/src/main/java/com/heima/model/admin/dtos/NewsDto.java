package com.heima.model.admin.dtos;

import com.heima.model.wemedia.pojos.WmNews;
import lombok.Data;

@Data
public class NewsDto extends WmNews {
    private String authorName;
}
