package com.pofo.backend.domain.notice.service;

import java.util.List;
import java.util.stream.Collectors;

import com.pofo.backend.common.exception.ServiceException;
import com.pofo.backend.domain.notice.dto.NoticeRequestDto;
import com.pofo.backend.domain.notice.dto.NoticeResponseDto;
import com.pofo.backend.domain.notice.entity.Notice;
import com.pofo.backend.domain.notice.repository.NoticeRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoticeService {

	private final NoticeRepository noticeRepository;

	@Transactional
	public NoticeResponseDto create(NoticeRequestDto noticeRequestDto) {

		Notice notice = Notice.builder()
			.subject(noticeRequestDto.getSubject())
			.content(noticeRequestDto.getContent())
			.build();

		noticeRepository.save(notice);
		return new NoticeResponseDto(notice);
	}

	@Transactional
	public NoticeResponseDto update(Long id, NoticeRequestDto noticeRequestDto) {

		Notice notice = this.noticeRepository.findById(id)
			.orElseThrow(() -> new ServiceException("404", "해당 공지사항을 찾을 수 없습니다."));
		notice.update(noticeRequestDto.getSubject(), noticeRequestDto.getContent());

		return new NoticeResponseDto(notice);
	}

	@Transactional
	public void delete(Long id) {

		Notice notice = this.noticeRepository.findById(id)
			.orElseThrow(() -> new ServiceException("404", "해당 공지사항을 찾을 수 없습니다."));

		this.noticeRepository.delete(notice);
	}

	@Transactional(readOnly = true)
	public NoticeResponseDto findById(Long id) {

		Notice notice = noticeRepository.findById(id)
			.orElseThrow(() -> new ServiceException("404", "해당 공지사항을 찾을 수 없습니다."));

		return new NoticeResponseDto(notice);
	}

	@Transactional(readOnly = true)
	public List<NoticeResponseDto> findAll() {

		List<Notice> notices = this.noticeRepository.findAllByOrderByCreatedAtDesc();
		return notices.stream()
			.map(NoticeResponseDto::new)
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public Long count() {
		return this.noticeRepository.count();
	}
}
