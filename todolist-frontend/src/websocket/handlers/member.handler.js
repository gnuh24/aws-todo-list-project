import { EVENT } from "../../event/EventType";

/**
 * Handle realtime member events of active project
 */
export function handleMemberEvent(
    event,
    {
        activeProject,
        setMembers,
        currentUserId,
        navigate,
    }
) {
    const { eventType, payload } = event;

    if (!activeProject) return;

    switch (eventType) {

        /* ================= ADD ================= */
        case EVENT.PROJECT_MEMBER_ADDED: {
            const member = payload?.member;
            if (!member) return;

            setMembers(prev => {
                if (prev.some(m => m.id === member.id)) return prev;
                return [...prev, member];
            });
            break;
        }

        /* ================= ACCEPT ================= */
        case EVENT.PROJECT_MEMBER_ACCEPTED: {
            const member = payload?.member;
            if (!member) return;

            setMembers(prev =>
                prev.map(m =>
                    m.id === member.id
                        ? { ...m, status: member.status }
                        : m
                )
            );
            break;
        }

        /* ================= REMOVE / DECLINED ================= */
        case EVENT.PROJECT_MEMBER_REMOVED:
        case EVENT.PROJECT_MEMBER_DECLINED: {
            const member = payload?.member;
            if (!member) return;

            // ❗ nếu chính user hiện tại bị kick / declined
            if (member.accountId === currentUserId) {
                navigate("/app/today"); // hoặc /app/projects, /app/archive
                return;
            }

            setMembers(prev =>
                prev.filter(m => m.id !== member.id)
            );
            break;
        }

        /* ================= ROLE UPDATE ================= */
        case EVENT.PROJECT_MEMBER_ROLE_UPDATED: {
            const member = payload?.member;
            if (!member) return;

            setMembers(prev =>
                prev.map(m =>
                    m.id === member.id
                        ? { ...m, role: member.role }
                        : m
                )
            );
            break;
        }

        default:
            break;
    }
}
