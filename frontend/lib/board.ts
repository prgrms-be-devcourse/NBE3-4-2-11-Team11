// import api from '../utils/api';  // 토큰 자동 처리 및 인터셉터 설정된 API 클라이언트 사용
// import { RsData } from '../utils/types';
// import {decodeJWT} from '../utils/decodeJWT';
// // import { getAccessToken } from '../utils/token';  // 토큰 가져오기

// /** 게시글 타입 정의  ResponseDto랑 맞춤*/
// export interface Post {
//   id: number;  // 게시글 ID
//   title: string;
//   content: string;
//   createdAt: string;  // 작성일자 필드 추가
//   user: {   
//     id: number;
//     email: string;
//     nickname?: string;  
//   };
// }
//  interface PostList {
//   currentPage: number;
//   totalPages: number;
//   totalItems: number;
//   boards: Post[];
// }







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



// // ✅ 게시글 작성 (email 제거)
// export const createPost = async (postData: { title: string; content: string }): Promise<Post> => {
//   const response = await api.post<RsData<Post>>('/user/boards', postData, {
//     withCredentials: true, // ✅ 로그인된 사용자 정보 자동 포함
//   });
//   return response.data.data;
// };


// // ✅ 게시글 수정 (email 제거)(특정 게시글 ID로 수정)
// export const updatePost = async (id: number, postData: { title: string; content: string }): Promise<Post> => {
//   const response = await api.patch<RsData<Post>>(`/user/boards/${id}`, postData, {
//     withCredentials: true, // ✅ 로그인된 사용자 정보 자동 포함
//   });
//   return response.data.data;
// };
// // // 게시글 수정 (특정 게시글 ID로 수정)
// // export const updatePost = async (id: number, postData: { title: string; content: string; email?: string }): Promise<Post> => {
// //   const response = await api.patch<RsData<Post>>(`/user/boards/${id}`, postData);
// //   return response.data.data;
// // };

// // 게시글 삭제 (특정 게시글 ID로 삭제)
// export const deletePost = async (id: number): Promise<{ message: string }> => {
//   const response = await api.delete<RsData<{ message: string }>>(`/user/boards/${id}`);
//   return response.data.data;
// };



import api from '../utils/api';  // 토큰 자동 처리 및 인터셉터 설정된 API 클라이언트 사용
import { RsData } from '../utils/types';
import { decodeJWT } from '../utils/decodeJWT';
// import { InternalAxiosRequestConfig } from "axios";  // ✅ 내부 설정 타입 사용

/** 게시글 타입 정의 (백엔드 `ResponseDto`에 맞춤) */
export interface Post {
  id: number;  // 게시글 ID
  title: string;
  content: string;
  createdAt: string;  // 작성일자 필드 추가
  user: {   
    id: number;
    email: string;
    nickname?: string;  
  };
}
interface PostList {
  currentPage: number;
  totalPages: number;
  totalItems: number;
  boards: Post[];
}

// 로그인된 유저 정보 가져오는 함수 (JWT에서 userId 추출)
const getUserId = (): number | null => {
  const token = localStorage.getItem('accessToken');  // JWT 토큰 가져오기
  if (!token) return null;
  const decoded = decodeJWT(token);
  return decoded?.userId || null;
};

// 게시글 목록 조회
export const getAllPosts = async (page: number = 1, size: number = 10): Promise<PostList> => {
  const response = await api.get<RsData<PostList>>('/user/boards', { params: { page, size } });
  return response.data.data;
};

// 게시글 상세 조회 (특정 게시글 ID로 조회)
export const getPostById = async (id: number): Promise<Post> => {
  const response = await api.get<RsData<Post>>(`/user/boards/${id}`);
  return response.data.data;
};

// 게시글 작성 (userId 추가)
export const createPost = async (postData: { title: string; content: string }): Promise<Post> => {
  const userId = getUserId();  // 로그인된 유저 ID 가져오기
  if (!userId) throw new Error('로그인이 필요합니다.');

  const response = await api.post<RsData<Post>>('/user/boards', 
    { ...postData, userId },  //userId 추가
    { withCredentials: true }
  );
  return response.data.data;
};

// 게시글 수정 (userId 추가)
export const updatePost = async (id: number, postData: { title: string; content: string }): Promise<Post> => {
  const userId = getUserId();
  if (!userId) throw new Error('로그인이 필요합니다.');

  const response = await api.patch<RsData<Post>>(`/user/boards/${id}`, 
    { ...postData, userId },  // userId 추가
    { withCredentials: true }
  );
  return response.data.data;
};

export const deletePost = async (id: number): Promise<{ message: string }> => {
  const userId = getUserId();
  if (!userId) throw new Error("로그인이 필요합니다.");

  // `axios.delete()`에서 `data`를 인식할 수 있도록 설정
  const config = {
    headers: {
      "Content-Type": "application/json",  // JSON 요청 명시
    },
    data: { userId },  // TypeScript가 인식할 수 있도록 data 명시
    withCredentials: true,
  } as any;  // 타입 에러 방지를 위해 `as any` 사용

  const response = await api.delete<RsData<{ message: string }>>(`/user/boards/${id}`, config);
  return response.data.data;
};