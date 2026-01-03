import { EVENT } from "../../event/EventType";

/**
 * Handle realtime section events of active project
 *
 * SectionEventDto:
 * { id, name, position }
 */
export function handleSectionEvent(
    event,
    { activeProject, setSections }
) {
    const { eventType, payload } = event;

    if (!activeProject) return;

    switch (eventType) {

        /* ================= CREATE ================= */
        case EVENT.SECTION_CREATED: {
            const section = payload?.section;
            if (!section) return;

            setSections(prev => {
                if (prev.some(s => s.id === section.id)) return prev;
                return [...prev, section];
            });
            break;
        }

        /* ================= NAME UPDATE ================= */
        case EVENT.SECTION_NAME_UPDATED: {
            const section = payload?.section;
            if (!section) return;

            setSections(prev =>
                prev.map(s =>
                    s.id === section.id
                        ? { ...s, name: section.name }
                        : s
                )
            );
            break;
        }

        /* ================= DELETE ================= */
        case EVENT.SECTION_DELETED: {
            const sectionId =
                payload?.sectionId || payload?.section?.id;
            if (!sectionId) return;

            setSections(prev =>
                prev.filter(s => s.id !== sectionId)
            );
            break;
        }

        /* ================= MOVE (POSITION UPDATE) ================= */
        case EVENT.SECTION_MOVED: {
            const section = payload?.section;
            if (!section) return;

            setSections(prev =>
                prev.map(s =>
                    s.id === section.id
                        ? { ...s, position: section.position }
                        : s
                )
            );
            break;
        }

        default:
            break;
    }
}
