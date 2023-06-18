import { useEffect, useState } from 'react';

interface Notification {
    id: number;
  message: string;
  date: string;
  opened: boolean;
}

const NotificationsPage = () => {
  const [notifications, setNotifications] = useState<Notification[]>([]);

  useEffect(() => {

     fetch("https://localhost:8081/api/v1/notif/readAll", {
        method: "GET",
                headers: {
                  "Content-type": "application/json",
                },
                credentials: "include"
              }).then(res => res.json())
                .then(notifications => {
                  // Sacuvaj notifikacije u Local Storage
                  localStorage.setItem('notifications', JSON.stringify(notifications));                
                  // Sacuvaj broj notifikacija u Local Storage
                  setNotifications(notifications);
                  localStorage.setItem('unopenedNotifications', "0");
                })
                .catch(error => {
                  console.error("Error while fetching notifications:", error);
                });

  },[]);



  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    const formattedDate = new Intl.DateTimeFormat('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    }).format(date);
    return formattedDate;
  };
  

  const getStatusClassName = (opened: boolean) => {
    return opened ? 'text-success' : 'text-danger';
  };

  return (
    <div>
      <h1>Notifikacije</h1>
      <table className="table">
        <thead>
          <tr>
            <th>Poruka</th>
            <th>Datum</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          {notifications.map((notification) => (
            <tr key={notification.date}>
              <td>{notification.message}</td>
              <td>{formatDate(notification.date)}</td>
              <td>
                <span className={getStatusClassName(notification.opened)}>
                  {notification.opened ? 'Pročitano' : 'Nepročitano'}
                </span>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default NotificationsPage;
