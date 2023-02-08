package com.itheima.sh.stock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public  class Account {
    private Integer id;
    private String userName;
    private String address;
}