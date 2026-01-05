import { EVENT } from "../../event/EventType";

/**
 * Handle realtime TASK events
 * - Update sections.tasks
 * - Update taskDetail nếu task đang mở
 */
export function handleTaskEvent(
    event,
    {
        activeProject,
        setSections,
        taskDetail,
        setTaskDetail,
        actorId
    }
) {
    const { eventType, payload } = event;
    const task = payload?.task;

    if (!activeProject || !task) return;
    if (task.idProject !== activeProject.id) return;

    // 👉 chính mình thao tác → bỏ qua websocket
    if (payload?.actor?.id === actorId) return;

    /* =====================================================
     * 📌 CREATE / RESTORE
     * ===================================================== */
    if (
        eventType === EVENT.TASK_CREATED ||
        eventType === EVENT.TASK_RESTORED
    ) {
        setSections(prev =>
            prev.map(section => {
                if (section.id !== task.idSection) return section;

                // tránh add trùng
                if (section.tasks?.some(t => t.id === task.id)) {
                    return section;
                }

                return {
                    ...section,
                    tasks: [...(section.tasks || []), task]
                };
            })
        );
        return;
    }

    /* =====================================================
     * 📌 UPDATE (content / status / priority / assignee…)
     * ===================================================== */
    if (
        eventType === EVENT.TASK_UPDATED ||
        eventType === EVENT.TASK_STATUS_UPDATED ||
        eventType === EVENT.TASK_PRIORITY_UPDATED ||
        eventType === EVENT.TASK_RELATIONSHIP_UPDATED ||
        eventType === EVENT.TASK_ASSIGNED ||
        eventType === EVENT.TASK_UNASSIGNED
    ) {
        // update sections
        setSections(prev =>
            prev.map(section => {
                if (!section.tasks?.some(t => t.id === task.id)) {
                    return section;
                }

                return {
                    ...section,
                    tasks: section.tasks.map(t =>
                        t.id === task.id ? { ...t, ...task } : t
                    )
                };
            })
        );

        // update task detail nếu đang mở
        if (taskDetail?.id === task.id) {
            setTaskDetail(prev => ({
                ...prev,
                ...task
            }));
        }
        return;
    }

    /* =====================================================
     * 📌 MOVE SECTION
     * ===================================================== */
    if (eventType === EVENT.TASK_SECTION_UPDATED) {
        setSections(prev => {
            let movedTask = null;

            // 1️⃣ remove task khỏi section cũ
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

            // 2️⃣ add task vào section mới
            return removed.map(section => {
                if (section.id !== task.idSection) return section;

                return {
                    ...section,
                    tasks: [...(section.tasks || []), movedTask || task]
                };
            });
        });

        // update task detail nếu đang mở
        if (taskDetail?.id === task.id) {
            setTaskDetail(prev => ({
                ...prev,
                ...task
            }));
        }
        return;
    }

    /* =====================================================
     * 📌 DELETE / ARCHIVE
     * ===================================================== */
    if (
        eventType === EVENT.TASK_DELETED ||
        eventType === EVENT.TASK_ARCHIVED
    ) {
        const taskId = task.id;

        setSections(prev =>
            prev.map(section => ({
                ...section,
                tasks: (section.tasks || []).filter(t => t.id !== taskId)
            }))
        );

        // nếu task đang mở → đóng
        if (taskDetail?.id === taskId) {
            setTaskDetail(null);
        }
        return;
    }
}
