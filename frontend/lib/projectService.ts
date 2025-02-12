import { useAuthStore } from "@/store/authStore";

const BASE_URL = "/api/v1/user";

// ✅ 공통 요청 옵션 함수 (JWT 토큰 포함)
const getAuthHeaders = () => {
  const token = useAuthStore.getState().accessToken; // ✅ Zustand에서 accessToken 가져오기
  if (!token) {
    console.warn("❌ JWT 토큰 없음, 인증 필요");
    return null;
  }

  return {
    "Content-Type": "application/json",
    Authorization: `Bearer ${token}`, // ✅ JWT 토큰 포함
  };
};

// ✅ 프로젝트 생성 (POST)
export const createProject = async (projectData: any) => {
  const token = useAuthStore.getState().accessToken;
  if (!token) {
    console.warn("❌ JWT 토큰 없음, 인증 필요");
    return { resultCode: "401", message: "Unauthorized" };
  }

  console.log("📢 [createProject] 요청 데이터:", JSON.stringify(projectData, null, 2));

  try {
    const res = await fetch("/api/v1/user/project", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(projectData),
    });

    console.log("📢 [createProject] API 응답 상태 코드:", res.status);

    // ✅ JSON 변환 전에 빈 응답 체크
    const text = await res.text();
    if (!text) {
      console.error("❌ 빈 응답 데이터");
      return { resultCode: "500", message: "서버 응답이 비어 있습니다." };
    }

    let data;
    try {
      data = JSON.parse(text);
    } catch (error) {
      console.error("❌ JSON 변환 오류:", error);
      return { resultCode: "500", message: "서버 응답이 올바르지 않습니다." };
    }

    console.log("📢 [createProject] API 응답 데이터:", data);

    // ✅ 응답 데이터 검증
    if (!data || typeof data !== "object" || !data.resultCode) {
      console.error("❌ 예상치 못한 응답 구조:", data);
      return { resultCode: "500", message: "서버 응답이 올바르지 않습니다." };
    }

    // ✅ 프로젝트 생성 성공 시 명확한 데이터 반환
    if (data.resultCode === "201" && data.data && data.data.projectId) {
      return { resultCode: "201", message: "Success", projectId: data.data.projectId };
    }

    // ❌ 실패 응답 반환
    return { resultCode: "500", message: data?.message || "알 수 없는 오류 발생" };
  } catch (error) {
    console.error("❌ 프로젝트 생성 중 오류 발생:", error);
    return { resultCode: "500", message: "Internal Server Error" };
  }
};



// ✅ 프로젝트 전체 조회 (GET)
export const getProjects = async () => {
  const token = useAuthStore.getState().accessToken;
  console.log("✅ 현재 저장된 accessToken:", token);
  if (!token) {
    console.warn("❌ JWT 토큰 없음, 인증 필요");
    return { code: "401", message: "Unauthorized" };
  }

  try {
    const res = await fetch("/api/v1/user/projects", {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        "Authorization": "Bearer " + token,
      },
    });

    console.log("📢 [getProjects] API 응답 상태 코드:", res.status);

    const data = await res.json();
    console.log("📢 [getProjects] API 응답 데이터:", data);

    if (!data || typeof data !== "object" || !data.data) {
      console.error("❌ 예상치 못한 응답 구조:", data);
      return { code: "500", message: "서버 응답이 올바르지 않습니다." };
    }

    return { code: "200", data: data.data };
  } catch (error) {
    console.error("❌ 프로젝트 목록 조회 중 오류 발생:", error);
    return { code: "500", message: "Internal Server Error" };
  }
};


// ✅ 프로젝트 단건 조회 (GET)
export const getProjectById = async (projectId: string) => {
  const headers = getAuthHeaders();
  if (!headers) return { code: "401", message: "Unauthorized" };

  try {
    const res = await fetch(`${BASE_URL}/projects/${projectId}`, {
      method: "GET",
      headers, // ✅ JWT 토큰 추가
    });

    const data = await res.json();
    console.log("📢 [getProjectById] API 응답 데이터:", data);

    if (res.ok) {
      return data;  // 200 OK 응답일 경우 data를 반환
    } else {
      return { resultCode: "500", message: "프로젝트 조회 실패" }; // 실패 시 에러 메시지 반환
    }
  } catch (error) {
    console.error("❌ 프로젝트 조회 실패:", error);
    return { resultCode: "500", message: "프로젝트 조회 중 오류가 발생했습니다." }; // 예외 처리
  }
};


// ✅ 프로젝트 수정 (PUT)
export const updateProject = async (projectId: string, projectData: any) => {
  const headers = getAuthHeaders();
  if (!headers) return { code: "401", message: "Unauthorized" };

  const res = await fetch(`${BASE_URL}/projects/${projectId}`, {
    method: "PUT",
    headers,
    body: JSON.stringify(projectData),
  });

  return res.json();
};

// ✅ 프로젝트 삭제 (DELETE)
export const deleteProject = async (projectId: string) => {
  const headers = getAuthHeaders();
  if (!headers) return { code: "401", message: "Unauthorized" };

  const res = await fetch(`${BASE_URL}/projects/${projectId}`, {
    method: "DELETE",
    headers, // ✅ JWT 토큰 추가
  });

  return res.json();
};
