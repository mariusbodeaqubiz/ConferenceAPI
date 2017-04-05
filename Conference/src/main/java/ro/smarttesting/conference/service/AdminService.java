package ro.smarttesting.conference.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

/**
 * Created by andreicontan on 06/01/2017.
 */


@Service
public class AdminService {

    @PreAuthorize("hasRole(@roles.ADMIN)")
    public boolean ensureAdmin(){
        return true;
    }
}
