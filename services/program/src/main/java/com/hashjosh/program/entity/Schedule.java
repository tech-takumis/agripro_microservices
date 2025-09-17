package com.hashjosh.workflow.model;

import com.hashjosh.workflow.enums.ScheduleType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@ToString
@Table(name = "schedules")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ScheduleType type;
    @Column(name = "schedule_date", nullable = false)
    private LocalDateTime scheduleDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private SchedulePriority priority;


}
