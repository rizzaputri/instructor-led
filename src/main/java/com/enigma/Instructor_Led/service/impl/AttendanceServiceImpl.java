package com.enigma.Instructor_Led.service.impl;

import com.enigma.Instructor_Led.dto.request.CreateAttendanceRequest;
import com.enigma.Instructor_Led.dto.response.*;
import com.enigma.Instructor_Led.entity.*;
import com.enigma.Instructor_Led.repository.AttendanceRepository;
import com.enigma.Instructor_Led.service.AttendanceDetailService;
import com.enigma.Instructor_Led.service.AttendanceService;
import com.enigma.Instructor_Led.service.ScheduleService;
import com.enigma.Instructor_Led.service.TraineeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final ScheduleService scheduleService;
    private final TraineeService traineeService;
    private final AttendanceDetailService attendanceDetailService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public AttendanceResponse create(CreateAttendanceRequest attendanceRequest) {
        // Mendapatkan jadwal berdasarkan ID
        Schedule scheduleById = scheduleService.getById(attendanceRequest.getScheduleId());

        // Membuat entitas Attendance
        Attendance attendance = Attendance.builder()
                .schedule(scheduleById)
                .date(new Date())
                .build();

        // Menyimpan entitas Attendance
        attendanceRepository.saveAndFlush(attendance);

        // Membuat detail kehadiran berdasarkan request
        List<AttendanceDetail> attendanceDetails = attendanceRequest.getAttendanceDetails().stream()
                .map(detailRequest -> {
                    Trainee trainee = traineeService.getOneById(detailRequest.getTraineeId());

                    return AttendanceDetail.builder()
                            .attendance(attendance)
                            .trainee(trainee)
                            .build();
                }).toList();

        // Menyimpan attendance detail secara bulk
        attendanceDetailService.createBulk(attendanceDetails);
        attendance.setAttendanceDetails(attendanceDetails);

        // Re-fetch schedule to include full information (including documentationImages)
        Schedule scheduleWithDetails = scheduleService.getById(attendanceRequest.getScheduleId());

        // Membuat response untuk AttendanceDetail
        List<AttendanceDetailResponse> attendanceDetailResponse = attendanceDetails.stream()
                .map(detail -> {
                    // Membuat response untuk Attendance
                    return AttendanceDetailResponse.builder()
                            .id(detail.getId())
                            .traineeId(detail.getTrainee().getId())
                            .build();
                }).toList();

        ScheduleResponse scheduleResponse = ScheduleResponse.builder()
                .id(attendance.getSchedule().getId())
                .date(attendance.getSchedule().getDate())
                .topic(attendance.getSchedule().getTopic())
                .linkSchedule(attendance.getSchedule().getLink())
                .trainerId(attendance.getSchedule().getTrainer().getId())
                .programmingLanguageId(attendance.getSchedule().getProgrammingLanguage().getId())
                .documentationImages(attendance.getSchedule().getDocumentationImages().stream().map(
                                doc -> DocumentationImageResponse.builder()
                                        .id(doc.getId())
                                        .link(doc.getLink())
                                        .scheduleId(doc.getSchedule().getId())
                                        .build())
                        .toList())
                .build();

        return AttendanceResponse.builder()
                .id(attendance.getId())
                .schedule(scheduleResponse)
                .attendanceDate(attendance.getDate())
                .attendanceDetails(attendanceDetailResponse)
                .build();

    }

    @Transactional(readOnly = true)
    @Override
    public List<AttendanceResponse> getAll() {
        List<Attendance> attendanceAll = attendanceRepository.findAll();
        return attendanceAll.stream().map(attendance -> {
            List<AttendanceDetailResponse> attendanceDetailResponse = attendance.getAttendanceDetails().stream()
                    .map(detail -> {
                        return AttendanceDetailResponse.builder()
                                .id(detail.getId())
                                .traineeId(detail.getTrainee().getId())
                                .build();
                    }).toList();

            ScheduleResponse scheduleResponse = ScheduleResponse.builder()
                    .id(attendance.getSchedule().getId())
                    .date(attendance.getSchedule().getDate())
                    .topic(attendance.getSchedule().getTopic())
                    .trainerId(attendance.getSchedule().getTrainer().getId())
                    .linkSchedule(attendance.getSchedule().getLink())
                    .programmingLanguageId(attendance.getSchedule().getProgrammingLanguage()!= null ?
                            attendance.getSchedule().getProgrammingLanguage().getId() : null)
                    .documentationImages(attendance.getSchedule().getDocumentationImages().stream().map(
                                    doc -> DocumentationImageResponse.builder()
                                            .id(doc.getId())
                                            .link(doc.getLink())
                                            .scheduleId(doc.getSchedule().getId())
                                            .build())
                            .toList())
                    .build();

            return AttendanceResponse.builder()
                    .id(attendance.getId())
                    .schedule(scheduleResponse)
                    .attendanceDate(attendance.getDate())
                    .attendanceDetails(attendanceDetailResponse)
                    .build();
        }).toList();
    }
}
