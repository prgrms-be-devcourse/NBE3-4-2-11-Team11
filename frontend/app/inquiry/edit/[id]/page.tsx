'use client';

import axios from "axios";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { useParams } from "next/navigation";

type InquiryUpdateResponse = {
    id: number;
};

const InquiryEditPage = () => {
  const { id } = useParams();
  const [inquiry, setInquiry] = useState<{ subject: string; content: string }>({ subject: "", content: "" });
  const [loading, setLoading] = useState(true);
  const [token, setToken] = useState<string | null>(null); // token 상태 정의
  const router = useRouter();

  useEffect(() => {
    const fetchInquiry = async () => {

      try {
        const response = await axios.get<InquiryUpdateResponse>(`/api/v1/common/inquiries/${id}`, { withCredentials: true });
        setInquiry(response.data.data);
      } catch (error) {
        console.error('Error fetching inquiry:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchInquiry();
  }, [id]);

  const handleUpdate = async (updatedData) => {

    try {
      const response = await axios.patch(`/api/v1/user/inquiries/${id}`, updatedData, { withCredentials: true });
      alert('문의사항이 성공적으로 수정되었습니다!');
      router.push(`/inquiry/${id}`);
    } catch (error) {
      console.error('Error updating inquiry:', error);
      alert('수정 실패: 알 수 없는 오류가 발생했습니다.');
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    handleUpdate(inquiry);
  };

  if (loading) {
    return <div>로딩 중...</div>;
  }

  return (
    <div className="max-w-5xl mx-auto p-4">
      <h1 className="text-2xl font-bold mb-4">문의사항 수정</h1>
      <form onSubmit={handleSubmit}>
        <div className="mb-4">
          <label className="block text-grey-700">제목</label>
          <input
            type="text"
            value={inquiry.subject}
            onChange={(e) => setInquiry({ ...inquiry, subject: e.target.value })}
            className="w-full px-3 py-2 border rounded"
            required
          />
        </div>
        <div className="mb-4">
          <label className="block text-gray-700">내용</label>
          <textarea
            value={inquiry.content}
            onChange={(e) => setInquiry({ ...inquiry, content: e.target.value })}
            className="w-full px-3 py-2 border rounded"
            required
          />
        </div>
        <button type="submit" className="bg-blue-500 text-white px-4 py-2 rounded ml-auto block">
          수정하기
        </button>
      </form>
    </div>
  );
};

export default InquiryEditPage; 