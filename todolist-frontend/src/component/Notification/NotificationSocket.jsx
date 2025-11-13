import {useEffect, useState} from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import {useNotifications} from "../../layout/MainLayout";


export default function NotificationSocket() {
    
    const { setCountNotificationsUnRead, setNotifications, pageSizeNotification, setTotalPagesNotification, setNewNotificationFormWebsocket  } = useNotifications();

    const [jwtToken] = useState(JSON.parse(localStorage.getItem("USER_INFO"))?.token);
    
    const UpdateNewNotification = (newNotification) => {
        console.log("Receive new message: " + newNotification);
        setCountNotificationsUnRead((prev) => prev + 1);
        setNotifications((prev) => {
            const updatedList = [newNotification, ...prev];

            // Update tổng số trang trước
            setTotalPagesNotification(Math.ceil(updatedList.length / pageSizeNotification));

            // Nếu danh sách vượt quá 5 phần tử thì xóa phần tử cuối để bảo toàn phân trang
            if (updatedList.length > pageSizeNotification) {
                updatedList.pop(); // hoặc dùng slice để an toàn
            }

            return updatedList;
        });
        setNewNotificationFormWebsocket(newNotification);
    }

    useEffect(() => {
        // 1️⃣ Tạo kết nối SockJS
        const socket = new SockJS("http://localhost:8080/api/notification/ws");

        // 2️⃣ Tạo STOMP client
        const client = new Client({
            webSocketFactory: () => socket,
            connectHeaders: { Authorization: "Bearer " + jwtToken },
            // debug: (str) => console.log("[STOMP Debug] " + str),
            reconnectDelay: 5000,
            onConnect: (frame) => {
                console.log("✅ Connected WebSocket");

                // 🔔 Đăng ký kênh cá nhân (private)
                client.subscribe("/user/private", (msg) => {
                    try {
                        const notification = JSON.parse(msg.body);
                        UpdateNewNotification(notification);
                    } catch (e) {
                        console.error("Parse error:", e);
                    }
                });

                // 🌍 Đăng ký kênh toàn cục (global)
                client.subscribe("/global", (msg) => {
                    try {
                        const notification = JSON.parse(msg.body);
                        UpdateNewNotification(notification);
                    } catch (e) {
                        console.error("Parse error:", e);
                    }
                });
            },

            // Khi có lỗi kết nối STOMP
            onStompError: (frame) => {
                console.error("STOMP ERROR:", frame);
            },

            // Khi disconnect
            onDisconnect: () => {
                console.log("⚠️ WebSocket disconnected");
            },
        });

        // 4️⃣ Kích hoạt kết nối
        client.activate();

        // 5️⃣ Cleanup khi component unmount
        return () => {
            console.log("🧹 Cleanup WebSocket");
            client.deactivate();
        };
    }, [jwtToken]); // phụ thuộc token hoặc hàm addMessage

    return null; // Component không render gì, chỉ giữ kết nối
}
