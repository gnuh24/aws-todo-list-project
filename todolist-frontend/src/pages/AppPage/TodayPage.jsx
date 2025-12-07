import { useEffect, useState } from "react";
import MainLayout from "../../layout/MainLayout";
import TodayHeader from "../../component/Header/TodayHeader";
import { https_taskflow } from "../../service/api";

function getDateOnly(dateStr) {
  if (!dateStr) return null;
  return dateStr.split("T")[0];
}

function formatDate(dateStr) {
  if (!dateStr) return "";
  const [y, m, d] = dateStr.split("T")[0].split("-");
  return `${d}/${m}/${y}`;
}

export default function TodayPage() {
  const [todayTasks, setTodayTasks] = useState([]);
  const [overdueTasks, setOverdueTasks] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadAllTasks = async () => {
      try {
        setLoading(true);

        // -------------------------
        // STEP 1: Lấy danh sách project
        // -------------------------
        const res = await https_taskflow.get("/v1/projects");
        const projects = res.data?.data || [];

        let allTasks = [];

        // -------------------------
        // STEP 2: Với mỗi project → get /v1/projects/{id}
        // -------------------------
        for (const p of projects) {
          try {
            const detail = await https_taskflow.get(`/v1/projects/${p.id}`);

            const sections = detail.data?.data?.sections || [];

            sections.forEach((sec) => {
              const tasks = Array.isArray(sec.tasks) ? sec.tasks : [];
              allTasks.push(...tasks);
            });
          } catch (err) {
            console.error("❌ Error project detail:", p.id, err);
          }
        }

        console.log("📌 ALL TASKS:", allTasks);

        const today = new Date().toISOString().split("T")[0];
        const todayList = [];
        const overdueList = [];

        allTasks.forEach((t) => {
          const d = getDateOnly(t.deadline);
          if (!d) return;

          if (d === today) todayList.push(t);
          if (d < today) overdueList.push(t);
        });

        setTodayTasks(todayList);
        setOverdueTasks(overdueList);
      } catch (err) {
        console.error("❌ Fetch error:", err);
      } finally {
        setLoading(false);
      }
    };

    loadAllTasks();
  }, []);

  const todayLabel = new Intl.DateTimeFormat("en-GB", {
    weekday: "long",
    day: "numeric",
    month: "short",
  }).format(new Date());

  return (
    <MainLayout>
      <div className="min-h-screen bg-white px-6 pb-10">
        <TodayHeader />
 

        {loading && <p>Loading...</p>}

        {/* ========== OVERDUE TASKS ========== */}
        {overdueTasks.length > 0 && (
          <div className="mt-6">
            <p className="text-sm font-semibold text-gray-600 mb-2">Overdue</p>

            {overdueTasks.map((t) => (
              <div key={t.id} className="py-3 border-b">
                <p className="font-medium">{t.title}</p>
                <p className="text-xs text-gray-600">{t.description}</p>
                <p className="text-xs text-red-500">{formatDate(t.deadline)}</p>
              </div>
            ))}
          </div>
        )}

        {/* ========== TODAY TASKS ========== */}
        <div className="mt-8">
          <p className="text-sm font-semibold text-gray-600 mb-2">
            {todayLabel}
          </p>

          {!loading && todayTasks.length === 0 && (
            <p className="text-gray-500 text-sm">No tasks for today 🎉</p>
          )}

          {todayTasks.map((t) => (
            <div key={t.id} className="py-3 border-b">
              <p className="font-medium">{t.title}</p>
              <p className="text-xs text-gray-600">{t.description}</p>
            </div>
          ))}
        </div>
      </div>
    </MainLayout>
  );
}
