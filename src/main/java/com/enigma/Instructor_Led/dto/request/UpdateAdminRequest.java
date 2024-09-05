package com.enigma.Instructor_Led.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateAdminRequest {

    @NotBlank(message = "Id is required")
    private String id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    @Pattern(regexp = "^08\\d{9,11}$", message = "nomor telepon hasus valid dan diawali dengan '08' diikuti oleh 9 hingga 11 angka")
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "address is required")
    private String address;
}
