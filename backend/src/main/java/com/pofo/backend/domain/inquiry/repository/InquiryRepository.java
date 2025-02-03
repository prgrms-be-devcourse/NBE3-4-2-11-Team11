package com.pofo.backend.domain.inquiry.repository;

import com.pofo.backend.domain.inquiry.entity.Inquiry;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
}
