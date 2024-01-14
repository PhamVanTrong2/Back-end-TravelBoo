package com.bootravel.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import java.util.Date;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class ContractsEntity {
    private Long id;
    private String name;
    private Integer businessAdminId;
    private String content;
    private Date startDate;
    private Date endDate;
    private Boolean status;
    private Date createdDate;
}
