// import api from '../utils/api';  // 토큰 자동 처리 및 인터셉터 설정된 API 클라이언트 사용
// import { RsData } from '../utils/types';
// import {decodeJWT} from '../utils/decodeJWT';
// import { getAccessToken } from '../utils/token';  // 토큰 가져오기

// /** 게시글 타입 정의 */
// export interface Post {
//   id: number;  // 게시글 ID
//   title: string;
//   content: string;
//   createdAt: string;  // 작성일자 필드 추가
//   email: string;  // 작성자 이메일 추가
// }

// interface PostList {
//   currentPage: number;
//   totalPages: number;
//   totalItems: number;
//   boards: Post[];
// }

// // // JWT에서 사용자 닉네임 추출 함수
// // const extractNicknameFromToken = (): string | null => {
// //   const token = getAccessToken();
// //   if (!token) return null;

// //   try {
// //     const decoded: { nickname: string } = jwtDecode(token);
// //     return decoded.nickname;  // 디코딩된 토큰에서 닉네임 반환
// //   } catch (error) {
// //     console.error('토큰 디코딩 실패:', error);
// //     return null;
// //   }
// // };

// // 게시글 목록 조회
// export const getAllPosts = async (page: number = 1, size: number = 10): Promise<PostList> => {
//   const response = await api.get<RsData<PostList>>('/user/boards', { params: { page, size } });
//   return response.data.data;
// };

// // 게시글 상세 조회 (특정 게시글 ID로 조회)
// export const getPostById = async (id: number): Promise<Post> => {
//   const response = await api.get<RsData<Post>>(`/user/boards/${id}`);
//   return response.data.data;
// };

// // 게시글 작성 (로그인한 사용자의 이메일로 작성)
// // export const createPost = async (postData: { title: string; content: string; email: string }): Promise<Post> => {
// //   const token = getAccessToken();
// //   if (!token) throw new Error('로그인한 사용자 정보를 찾을 수 없습니다.');

// //   const decoded: any = decodeJWT(token);  // JWT 디코딩
// //   const email = decoded.sub;  // 이메일 정보 추출 (sub 필드)

// //   if (!email) throw new Error('사용자 이메일 정보를 찾을 수 없습니다.');

// //   const response = await api.post<RsData<Post>>('/user/boards', { ...postData, email });
// //   return response.data.data;
// export const createPost = async (postData: { title: string; content: string; email: string }): Promise<Post> => {
//   const response = await api.post<RsData<Post>>('/user/boards', postData);  // 이메일 포함된 데이터 전송
//   return response.data.data;
// };

// // 게시글 수정 (특정 게시글 ID로 수정)
// export const updatePost = async (id: number, postData: { title: string; content: string; email?: string }): Promise<Post> => {
//   const response = await api.patch<RsData<Post>>(`/user/boards/${id}`, postData);
//   return response.data.data;
// };

// // 게시글 삭제 (특정 게시글 ID로 삭제)
// export const deletePost = async (id: number): Promise<{ message: string }> => {
//   const response = await api.delete<RsData<{ message: string }>>(`/user/boards/${id}`);
//   return response.data.data;
// };



//************************************************* */
// import { useAuthStore } from "@/store/authStore";

// /** 게시글 타입 정의 */
// export interface Post {
//   id: number;  // 게시글 ID
//   title: string;
//   content: string;
//   createdAt: string;  // 작성일자 필드 추가
// }

// interface PostList {
//   currentPage: number;
//   totalPages: number;
//   totalItems: number;
//   boards: Post[];
// }

// /** 공통 응답 객체 */
// interface RsData<T> {
//   code: string;
//   message: string;
//   data: T;
// }

// /** 공통 요청 옵션 (쿠키 포함) */
// const getRequestOptions = (method: string, body?: any) => {
//   return {
//     method,
//     headers: {
//       "Content-Type": "application/json",
//     },
//     credentials: "include" as const, // ✅ 쿠키 포함
//     body: body ? JSON.stringify(body) : undefined,
//   };
// };

// // ✅ 게시글 목록 조회 (GET /api/v1/user/boards)
// export const getAllPosts = async (page: number = 1, size: number = 10): Promise<PostList> => {
//   const res = await fetch(`/api/v1/user/boards?page=${page}&size=${size}`, getRequestOptions("GET"));
//   const data: RsData<PostList> = await res.json();
//   return data.data;
// };

// // ✅ 게시글 상세 조회 (GET /api/v1/user/boards/{id})
// export const getPostById = async (id: number): Promise<Post> => {
//   const res = await fetch(`/api/v1/user/boards/${id}`, getRequestOptions("GET"));
//   const data: RsData<Post> = await res.json();
//   return data.data;
// };

// // ✅ 게시글 작성 (POST /api/v1/user/boards)
// export const createPost = async (postData: { title: string; content: string }): Promise<Post> => {
//   const res = await fetch(`/api/v1/user/boards`, getRequestOptions("POST", postData));
//   const data: RsData<Post> = await res.json();
//   return data.data;
// };

// // ✅ 게시글 수정 (PATCH /api/v1/user/boards/{id})
// export const updatePost = async (id: number, postData: { title: string; content: string }): Promise<Post> => {
//   const res = await fetch(`/api/v1/user/boards/${id}`, getRequestOptions("PATCH", postData));
//   const data: RsData<Post> = await res.json();
//   return data.data;
// };

// // ✅ 게시글 삭제 (DELETE /api/v1/user/boards/{id})
// export const deletePost = async (id: number): Promise<{ message: string }> => {
//   const res = await fetch(`/api/v1/user/boards/${id}`, getRequestOptions("DELETE"));
//   const data: RsData<{ message: string }> = await res.json();
//   return data.data;
// };


const API_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";

/** 게시글 타입 정의 */
export interface Post {
  id: number;
  title: string;
  content: string;
  createdAt: string;
  updatedAt?: string;  // ✅ 수정일자 (optional)
  userId?: number;  // ✅ 작성자 ID도 optional 처리
}

interface PostList {
  currentPage: number;
  totalPages: number;
  totalItems: number;
  boards: Post[];
}

/** 공통 응답 객체 */
interface RsData<T> {
  code: string;
  message: string;
  data: T;
}

/** 공통 요청 옵션 (쿠키 기반) */
const getRequestOptions = (method: string, body?: any) => ({
  method,
  headers: {
    "Content-Type": "application/json",
  },
  credentials: "include" as const, // ✅ 쿠키 포함
  body: body ? JSON.stringify(body) : undefined,
});

// ✅ 게시글 목록 조회 (GET /api/v1/user/boards)
export const getAllPosts = async (page: number = 1, size: number = 10): Promise<PostList> => {
  try {
    const res = await fetch(`${API_URL}/api/v1/user/boards?page=${page}&size=${size}`, getRequestOptions("GET"));
    
    if (!res.ok) {
      if (res.status === 400) {
        throw new Error("게시글이 존재하지 않습니다.");
      }
      throw new Error(`게시글 목록 불러오기 실패: ${res.status} ${res.statusText}`);
    }

    const data: RsData<PostList> = await res.json();
    return data.data;
  } catch (error) {
    console.error("❌ 게시글 목록 조회 실패:", error);
    throw error;
  }
};

// ✅ 게시글 상세 조회 (GET /api/v1/user/boards/{id})
export const getPostById = async (id: number): Promise<Post> => {
  try {
    const res = await fetch(`${API_URL}/api/v1/user/boards/${id}`, getRequestOptions("GET"));
    
    if (!res.ok) {
      if (res.status === 400) {
        throw new Error("해당 게시글이 존재하지 않습니다.");
      }
      throw new Error(`게시글 조회 실패: ${res.status} ${res.statusText}`);
    }

    const data: RsData<Post> = await res.json();
    return data.data;
  } catch (error) {
    console.error("❌ 게시글 상세 조회 실패:", error);
    throw error;
  }
};

// ✅ 게시글 작성 (POST /api/v1/user/boards)
export const createPost = async (postData: { title: string; content: string }): Promise<Post> => {
  try {
    const res = await fetch(`${API_URL}/api/v1/user/boards`, getRequestOptions("POST", postData));

    if (!res.ok) {
      throw new Error(`게시글 작성 실패: ${res.status} ${res.statusText}`);
    }

    const data: RsData<Post> = await res.json();
    return data.data;
  } catch (error) {
    console.error("❌ 게시글 작성 실패:", error);
    throw error;
  }
};

// ✅ 게시글 수정 (PATCH /api/v1/user/boards/{id})
export const updatePost = async (id: number, postData: { title: string; content: string }): Promise<Post> => {
  try {
    const res = await fetch(`${API_URL}/api/v1/user/boards/${id}`, getRequestOptions("PATCH", postData));

    if (!res.ok) {
      throw new Error(`게시글 수정 실패: ${res.status} ${res.statusText}`);
    }

    const data: RsData<Post> = await res.json();
    return data.data;
  } catch (error) {
    console.error("❌ 게시글 수정 실패:", error);
    throw error;
  }
};

//✅ 게시글 삭제 (DELETE /api/v1/user/boards/{id})
export const deletePost = async (id: number): Promise<{ message: string }> => {
  try {
    const res = await fetch(`${API_URL}/api/v1/user/boards/${id}`, getRequestOptions("DELETE"));

    if (!res.ok) {
      throw new Error(`게시글 삭제 실패: ${res.status} ${res.statusText}`);
    }

    const data: RsData<{ message: string }> = await res.json();
    return data.data;
  } catch (error) {
    console.error("❌ 게시글 삭제 실패:", error);
    throw error;
  }
// export const deletePost = async (id: number, userId: number): Promise<{ message: string }> => {
//   try {
//     console.log("🛠 게시글 삭제 요청:", id, userId);

//     const res = await fetch(`${API_URL}/api/v1/user/boards/${id}`, {
//       method: "DELETE",
//       headers: {
//         "Content-Type": "application/json",
//       },
//       credentials: "include", // ✅ JWT 포함 (필수)
//       body: JSON.stringify({ userId }), // ✅ 요청 바디 추가
//     });

//     console.log("🛠 게시글 삭제 응답 상태 코드:", res.status);

//     if (!res.ok) {
//       throw new Error(`게시글 삭제 실패: ${res.status} ${res.statusText}`);
//     }

//     const data: RsData<{ message: string }> = await res.json();
//     return data.data;
//   } catch (error) {
//     console.error("❌ 게시글 삭제 요청 실패:", error);
//     throw error;
//   }
};



