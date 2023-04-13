package com.itmsd.medical.services;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import com.itmsd.medical.entities.*;
import com.itmsd.medical.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.bytebuddy.utility.RandomString;


@Service("RdvService")
@Transactional
public class RdvService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private RendezVousRepository rendezVousRepository;
    private ScheduleRepository scheduleRepository;

    @Autowired
    public RdvService(UserRepository userRepository, RoleRepository roleRepository,
                      BCryptPasswordEncoder bCryptPasswordEncoder,ForumRepository forumRepository,RendezVousRepository rendezVousRepository, ScheduleRepository scheduleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.rendezVousRepository = rendezVousRepository;
        this.scheduleRepository = scheduleRepository;

    }

    public List<RendezVous> getAllRendezVousperso(long personnelId) {
        List<RendezVous> RdvPerso = rendezVousRepository.findByPersonnelId(personnelId);
        return RdvPerso;

    }

    public List<RendezVous> getAllRendezVousClient(long clientId) {
        List<RendezVous> RdvClient = rendezVousRepository.findByClientId(clientId);
        return RdvClient;

    }
    public Schedule getHisSchedule(long personnelId) {
        User personnel = userRepository.findUserById(personnelId);
        Schedule s = personnel.getSchedule();
        return s;

    }
  //  public List<Schedule> getHisSchedule(long personnelId) {
 //       User personnel = userRepository.findUserById(personnelId);
// return s;

  //  }

}
