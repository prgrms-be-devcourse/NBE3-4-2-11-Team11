"use client";

import { useEffect, useState } from 'react';
import { useParams } from 'next/navigation';
import axios from 'axios';
import styles from '../noticeDetail.module.css';

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

const NoticeDetailPage = () => {
  const { id } = useParams();
  const [notice, setNotice] = useState<NoticeDetailResponse | null>(null);

  console.log(`Current ID: ${id}`); // 디버깅 로그

  useEffect(() => {
    if (id) {
      const fetchNotice = async () => {
        try {
          const response = await axios.get<RsData<NoticeDetailResponse>>(`/api/v1/common/notices/${id}`);
          console.log('Response data:', response.data); // 디버깅 로그
          setNotice(response.data.data);
        } catch (error) {
          console.error('Error fetching notice:', error);
        }
      };

      fetchNotice();
    }
  }, [id]);

  if (!notice) return <div>Loading...</div>;

  return (
    <div className={styles.noticeContainer}>
      <div className={styles.noticeSubjectRow}>
        <h1 className={styles.noticeHeader}>{notice.subject}</h1>
        <p className={styles.noticeDate}>{new Date(notice.createdAt).toLocaleDateString('ko-KR')}</p>
      </div>
      <hr className={styles.noticeDivider}/>
      <p className={styles.noticeContent}>{notice.content}</p>
    </div>
  );
};

export default NoticeDetailPage;
