package ru.kiselev.erp.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kiselev.erp.dto.request.ManufacturerDto;
import ru.kiselev.erp.dto.response.UserDto;
import ru.kiselev.erp.model.Role;
import ru.kiselev.erp.model.User;
import ru.kiselev.erp.model.admin.Manufacturer;
import ru.kiselev.erp.repository.UserRepository;
import ru.kiselev.erp.repository.admin.ManufacturerRepository;

import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class AdminService {

    UserRepository userRepository;
    ManufacturerRepository manufacturerRepository;
    PasswordEncoder passwordEncoder;

    public List<UserDto> getAllUsers(){
        return userRepository.findAll()
                .stream().map(UserDto::new)
                .collect(Collectors.toList());
    }

    public void register(String username, String password) {

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.USER);

        userRepository.save(user);
    }

    public void deleteUserById(Long id){

        userRepository.deleteById(id);

    }

    public List<ManufacturerDto> getAllManufacturers(){
        return manufacturerRepository.findAll()
                .stream().map(ManufacturerDto::new)
                .collect(Collectors.toList());
    }

    public ManufacturerDto addManufacturer(ManufacturerDto manufacturerDto){

        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName(manufacturerDto.getName());
        manufacturer.setCode(manufacturerDto.getCode());

        manufacturerRepository.save(manufacturer);
        return manufacturerDto;
    }

    public void deleteManufacturerById(Long id){

        manufacturerRepository.deleteById(id);

    }

}
