package com.dmasso.multidwh.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
//@ConfigurationProperties
public class OltpConnectionProperties {
    private String url = "jdbc:postgresql://localhost:5432/postgres";
    String user = "postgres";
    String password = "password";
}
