package gt.com.archteam.springcloud.msvc.oauth.models;

import java.util.List;

import lombok.Data;

@Data
public class User {

    private Long id;

    private String username;

    private String password;

    private Boolean enabled;

    private boolean admin;

    private List<Role> roles;

    private String email;

}
