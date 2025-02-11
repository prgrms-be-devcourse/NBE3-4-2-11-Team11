"use client";

import { useEffect, useState } from 'react';
import Link from 'next/link';
import axios from 'axios';
import styles from './noticeList.module.css';

type NoticeDetailResponse = {
  id: number;
  subject: string;
  content: string;
  createdAt: string;
};

type RsData<T> = {
  resultCode: string;
  message: string;
  data: T;
};

const NoticePage = () => {
  const [notices, setNotices] = useState<NoticeDetailResponse[]>([]);

  useEffect(() => {
    const fetchNotices = async () => {
      try {
        const response = await axios.get<RsData<NoticeDetailResponse[]>>('/api/v1/common/notices');
        if (Array.isArray(response.data.data)) {
          setNotices(response.data.data);
        } else {
          console.error('Data is not an array:', response.data.data);
        }
      } catch (error) {
        console.error('Error fetching notices:', error);
      }
    };

    fetchNotices();
  }, []);

  return (
    <div className={styles.noticeContainer}>
      <h1 className={styles.noticeHeader}>공지사항</h1>
      <ul>
        {notices.map((notice) => (
          <li key={notice.id} className={styles.noticeBox}>
           <Link href={`/notice/${notice.id}`}>
            <div className={styles.noticeSubjectRow}>
              <div className={styles.noticeSubject}>{notice.subject}</div>
              <div className={styles.noticeDate}>{new Date(notice.createdAt).toLocaleDateString('ko-KR')}</div>
            </div>
            <p className={styles.noticeContent}>{notice.content}</p>
          </Link>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default NoticePage;

