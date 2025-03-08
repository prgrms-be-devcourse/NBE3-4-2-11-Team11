import Link from "next/link";

interface ProjectItemProps {
  project: {
    id: string; // ✅ id가 문자열로 올바르게 전달되는지 확인
    name: string;
    description: string;
  };
}

const ProjectItem: React.FC<ProjectItemProps> = ({ project }) => {
  console.log(`📢 [ProjectItem] 렌더링된 프로젝트 ID: ${project.id}`); // ✅ 프로젝트 ID 확인

  return (
    <li>
      <Link
        href={`/mypage/projects/${project.id}`}
        onClick={() => console.log(`📢 클릭된 프로젝트 ID: ${project.id}`)}
      >
        <h3>{project.name}</h3>
        <p>{project.description}</p>
      </Link>
    </li>
  );
};

export default ProjectItem;
