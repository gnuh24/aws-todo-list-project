import { EVENT } from "../../event/EventType";
import {CLIENT_ID} from "../../service/clientId";

/**
 * Handle realtime TASK events
 * - Update sections.tasks
 * - Update taskDetail nếu task đang mở
 */
export function handleTaskEvent(
    event,
    { activeProject, setSections, activeTaskId, setTaskDetail, actorId }
) {
    const { eventType, payload } = event;
    const task = payload?.task;

    if (!activeProject || !task) return;
    if (task.idProject !== activeProject.id) return;

    // 👉 chính mình thao tác → bỏ qua websocket
    if (payload?.actor?.clientId === CLIENT_ID) return;

    switch (eventType) {
        /* =====================================================
         * 📌 CREATE / RESTORE
         * ===================================================== */
        case EVENT.TASK_CREATED:
        case EVENT.TASK_RESTORED:
            setSections(prev =>
                prev.map(section => {
                    if (section.id !== task.idSection) return section;
                    if (section.tasks?.some(t => t.id === task.id)) return section; // tránh add trùng
                    return { ...section, tasks: [...(section.tasks || []), task] };
                })
            );
            break;

        /* =====================================================
         * 📌 UPDATE (content / status / priority / assignee…)
         * ===================================================== */
        case EVENT.TASK_UPDATED:
        case EVENT.TASK_STATUS_UPDATED:
        case EVENT.TASK_PRIORITY_UPDATED:
        case EVENT.TASK_RELATIONSHIP_UPDATED:
        case EVENT.TASK_ASSIGNED:
        case EVENT.TASK_UNASSIGNED:
            setSections(prev =>
                prev.map(section => {
                    if (!section.tasks?.some(t => t.id === task.id)) return section;
                    return {
                        ...section,
                        tasks: section.tasks.map(t => (t.id === task.id ? { ...t, ...task} : t))
                    };
                })
            );

            if (activeTaskId === task.id) {
                setTaskDetail(prev => ({ ...prev, ...task }));
            }
            break;

        /* =====================================================
         * 📌 MOVE SECTION
         * ===================================================== */
        case EVENT.TASK_SECTION_UPDATED:
            setSections(prev => {
                let movedTask = null;

                // remove task khỏi section cũ
                const removed = prev.map(section => {
                    if (!section.tasks?.some(t => t.id === task.id)) return section;
                    const newTasks = section.tasks.filter(t => {
                        if (t.id === task.id) {
                            movedTask = { ...t, ...task };
                            return false;
                        }
                        return true;
                    });
                    return { ...section, tasks: newTasks };
                });

                // add task vào section mới
                return removed.map(section => {
                    if (section.id !== task.idSection) return section;
                    return { ...section, tasks: [...(section.tasks || []), movedTask || task] };
                });
            });

            if (activeTaskId === task.id) {
                setTaskDetail(prev => ({ ...prev, ...task }));
            }
            break;

        /* =====================================================
         * 📌 DELETE / ARCHIVE
         * ===================================================== */
        case EVENT.TASK_DELETED:
        case EVENT.TASK_ARCHIVED:
            const taskId = task.id;
            setSections(prev =>
                prev.map(section => ({
                    ...section,
                    tasks: (section.tasks || []).filter(t => t.id !== taskId)
                }))
            );

            if (activeTaskId === taskId) {
                setTaskDetail(null);
            }
            break;

        default:
            break;
    }
}
