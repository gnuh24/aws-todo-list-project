// websocket/handlers/project.handler.js
import { EVENT } from "../../event/EventType";

export function handleProjectEvent(event, { activeProject, setActiveProject }) {
    const { eventType, payload } = event;

    if (!activeProject) return;

    switch (eventType) {

        case EVENT.PROJECT_UPDATED: {
            const project = payload?.project;
            if (!project) return;

            if (project.id !== activeProject.id) return;

            setActiveProject(prev => ({
                ...prev,
                ...project
            }));
            break;
        }

        case EVENT.PROJECT_ARCHIVED:
        case EVENT.PROJECT_DELETED: {
            const projectId = payload?.projectId || payload?.project?.id;
            if (!projectId) return;

            if (projectId !== activeProject.id) return;

            setActiveProject(null);

            // 🔔 báo cho WebSocketClient biết cần leave project
            return { type: "LEAVE_PROJECT" };
        }

        default:
            break;
    }
}
