import { EVENT } from "../../event/EventType";
import {CLIENT_ID} from "../../service/clientId";

/**
 * Handle realtime COMMENT events
 * - Chỉ update taskDetail.comments
 */
export function handleCommentEvent(
    event,
    { activeProject, activeTaskId, setTaskDetail, actorId }
) {
    const { eventType, payload } = event;
    const comment = payload?.comment;
    const taskId = comment?.taskId;


    if (!activeProject || !comment) return;

    // chỉ xử lý comment của task đang mở
    if (taskId !== activeTaskId) return;

    // 👉 chính mình thao tác → bỏ qua websocket
    if (payload?.actor?.clientId === CLIENT_ID) return;

    switch (eventType) {
        /* =====================================================
         * 💬 CREATE
         * ===================================================== */
        case EVENT.COMMENT_CREATED:
            setTaskDetail(prev => {
                if (!prev) return prev;
                if (prev.comments?.some(c => c.id === comment.id)) return prev;

                return {
                    ...prev,
                    comments: [...(prev.comments || []), comment]
                };
            });
            break;

        /* =====================================================
         * 💬 UPDATE
         * ===================================================== */
        case EVENT.COMMENT_UPDATED:
            setTaskDetail(prev => {
                if (!prev) return prev;

                return {
                    ...prev,
                    comments: prev.comments.map(c =>
                        c.id === comment.id ? { ...c, ...comment } : c
                    )
                };
            });
            break;

        /* =====================================================
         * 💬 DELETE
         * ===================================================== */
        case EVENT.COMMENT_DELETED:
            setTaskDetail(prev => {
                if (!prev) return prev;

                return {
                    ...prev,
                    comments: prev.comments.filter(c => c.id !== comment.id)
                };
            });
            break;

        default:
            break;
    }
}
