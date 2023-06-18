import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

interface Notification {
  id: number;
  message: string;
  date: string;
  opened: boolean;
}

class SocketService {
  static establishConnection() {
    const socket = new SockJS('https://localhost:8081/ws');
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {
      stompClient.subscribe('/logs/notif', (message) => {
        const notif = JSON.parse(message.body) as Notification; // Type assertion to Notification interface
        console.log(notif);
        
        // Čuvanje notifikacija u Local Storage
        const storedNotifications = JSON.parse(localStorage.getItem('notifications') || '[]') as Notification[];
        storedNotifications.push(notif); // Add the new notification to the array
        localStorage.setItem('notifications', JSON.stringify(storedNotifications));
         // Prikazivanje alerta
         window.alert(`New notification: ${notif.message}`);
      });
    });
  }
}

export default SocketService;

