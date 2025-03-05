// frontend/app/components/UserAgeDistributionChart.tsx
"use client";

import { Bar } from "react-chartjs-2";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from "chart.js";
import dayjs from "dayjs";

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

interface UserAgeDistributionChartProps {
  userStats: { age: string }[];
}

export default function UserAgeDistributionChart({ userStats }: UserAgeDistributionChartProps) {
  const currentYear = dayjs().year();
  const ageGroups: Record<string, number> = {};

  userStats.forEach((user) => {
    const birthYear = dayjs(user.age).year();
    const age = currentYear - birthYear;
    const group = `${Math.floor(age / 10) * 10}대`;
    ageGroups[group] = (ageGroups[group] || 0) + 1;
  });

  const labels = Object.keys(ageGroups).sort();
  const dataValues = labels.map(label => ageGroups[label]);

  const data = {
    labels,
    datasets: [
      {
        label: "연령대별 가입자 수",
        data: dataValues,
        backgroundColor: "rgba(54, 162, 235, 0.6)",
      },
    ],
  };

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false },
      title: { display: true, text: "사용자 연령대 분포", font: { size: 16 } },
    },
    scales: {
      x: { ticks: { font: { size: 12 } } },
      y: { ticks: { font: { size: 12 } }, beginAtZero: true },
    },
  };

  return (
    <div style={{ height: "300px", width: "100%" }}>
      <Bar data={data} options={options} />
    </div>
  );
}
