package com.bootravel.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


import java.io.Serializable;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeOfsEntity implements Serializable {
    private Long id;
    private Long userId;
    private Long managerId;
    private Long hotelId;
    private Boolean status;
}
