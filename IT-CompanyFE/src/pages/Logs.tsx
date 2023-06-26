import { useEffect, useRef, useState } from 'react';
import { CompatClient, Stomp } from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';
import { UseLoggedUser } from '../hooks/UseLoggedUserInformation';

interface Logs {
  date: string;
  type: string;
  component: string;
  message: string;
}

function LogViewer() {
  const [logs, setLogs] = useState<Array<Logs>>([]);
  const [filteredLogs, setFilteredLogs] = useState<Array<Logs>>([]);
  const [filterType, setFilterType] = useState<string>('');
  const loggedUser = UseLoggedUser();
  const [connect, setConnect] = useState(false);


  const sendMessage = (stompClient: CompatClient|null) => {
    if (stompClient) {
      try {
        stompClient.send('/app/logs', {}, JSON.stringify('neki string'));
      } catch (error) {
        console.log(error)
      }
    }
  };
  
  useEffect(() => {
      let stompClient!: CompatClient | null;
      if (!connect) {
        stompClient = establishConnection();
        setConnect(true);
      } else {
        stompClient = stompClientRef.current; // koristimo referencu na trenutni stompClient
      }
  
      // Ažuriranje logova na svake 5 sekunde
      const intervalId = setInterval(() => {
        sendMessage(stompClient);
      }, 5000);
  
      return () => {
        clearInterval(intervalId);
      };
    
  }, [connect]);
  const stompClientRef = useRef<CompatClient | null>(null!);

const establishConnection = () => {
  if (loggedUser?.role.name === "ADMINISTRATOR") {
  const socket = new SockJS('https://localhost:8081/ws');
  const stompClient = Stomp.over(socket);
  stompClientRef.current = stompClient;

  stompClient.connect({}, () => {
    stompClient.subscribe('/logs/front', (message) => {
      const logArray = JSON.parse(message.body);
      setLogs(logArray);
    });
  }, );
  
  return stompClient;}
  return null;
};

  

  useEffect(() => {
    filterLogs();
  }, [logs, filterType]);

  const filterLogs = () => {
    if (filterType === '') {
      setFilteredLogs(logs);
    } else {
      const filtered = logs.filter((log) => log.type === filterType);
      setFilteredLogs(filtered);
    }
  };

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    const formattedDate = date.toLocaleString('en-GB', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
    });
    return formattedDate;
  };

  return (
    <div className="container">
      <h1>Log Viewer</h1>
      <div className="mb-3">
        <label htmlFor="filterType" className="form-label">
          Filter by Type:
        </label>
        <select
          id="filterType"
          className="form-select"
          value={filterType}
          onChange={(e) => setFilterType(e.target.value)}
        >
          <option value="">All</option>
          <option value="INFO">INFO</option>
          <option value="ERROR">ERROR</option>
          <option value="DEBUG">DEBUG</option>
          <option value="WARN">WARN</option>
        </select>
      </div>
      <table className="table table-striped">
        <thead className="thead-dark">
          <tr>
            <th>Date</th>
            <th>Type</th>
            <th>Component</th>
            <th>Message</th>
          </tr>
        </thead>
        <tbody>
          {filteredLogs.map((log, index) => (
            <tr key={index}>
              <td>{formatDate(log.date)}</td>
              <td>{log.type}</td>
              <td>{log.component}</td>
              <td>{log.message}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default LogViewer;

