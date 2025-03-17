package com.rjproj.memberapp.service;

import com.rjproj.memberapp.model.Member;
import com.rjproj.memberapp.repository.MemberRepository;
import com.rjproj.memberapp.security.MemberDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(username).orElseThrow(() ->
                new UsernameNotFoundException("Member not found"));;
        return new MemberDetails(member);
    }

}
