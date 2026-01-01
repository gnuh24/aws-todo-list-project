import { createContext, useContext, useRef } from "react";

/**
 * EventBus:
 * - emit(eventType, data)
 * - subscribe(eventType, callback)
 */

const EventBusContext = createContext(null);

export function EventBusProvider({ children }) {
    // Lưu các listener, KHÔNG gây rerender
    const listenersRef = useRef({});

    // Phát event
    const emit = (eventType, data) => {
        const listeners = listenersRef.current[eventType];
        if (!listeners) return;

        listeners.forEach((cb) => cb(data));
    };

    // Đăng ký nghe event
    const subscribe = (eventType, callback) => {
        if (!listenersRef.current[eventType]) {
            listenersRef.current[eventType] = [];
        }

        listenersRef.current[eventType].push(callback);

        // trả về hàm unsubscribe
        return () => {
            listenersRef.current[eventType] =
                listenersRef.current[eventType].filter((cb) => cb !== callback);
        };
    };

    return (
        <EventBusContext.Provider value={{ emit, subscribe }}>
            {children}
        </EventBusContext.Provider>
    );
}

export function useEventBus() {
    return useContext(EventBusContext);
}
