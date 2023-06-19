import { useEffect, useState } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';

interface User {
  userId: number;
  firstname: string;
  lastname: string;
  email: string;
  phoneNumber: string;
  title: boolean;
  role: Role;
  address: Address;
}

interface Address {
  street: string;
  number: string;
  city: string;
  country: string;
}

interface Role {
  id: number;
  name: string;
}

function ViewAllCV ()  {
  const [users, setUsers] = useState<User[]>([]);
  // const pattern = /^[^d]/i; 

  useEffect(() => {
    fetch('https://localhost:8081/api/v1/user/getAll', {
      method: 'GET',
      headers: {
        'Content-type': 'application/json',
      },
      credentials: 'include',
    })
      .then((response) => response.json())
      .then((data) => {
        setUsers(data);
        console.log(data)
      })
      .catch((error) => console.log(error));
      
       
  }, []);

  const handleDownloadClick = (email: string) => {
    fetch(`https://localhost:8081/api/v1/user/downloadCV?email=`+email)
      .then(response => response.blob())
      .then(blob => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', 'CV.pdf');
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
      })
      .catch(error => {
        console.error('Greška prilikom preuzimanja fajla:', error);
      });
  };

  return (
    <div className="container">
      <h1>Employees</h1>
      <table className="table table-striped">
        <thead className="thead-dark">
          <tr>
            <th>First name</th>
            <th>Last name</th>
            <th>Email</th>
            <th>--</th>
          </tr>
        </thead>
        <tbody>
          {users.map((user) => (
            <tr key={user.userId}>
              <td>{user.firstname}</td>
              <td>{user.lastname}</td>
              <td>{user.email}</td>
              <td><button className="btn btn-primary" onClick={()=>handleDownloadClick(user.email)}>Download</button></td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default ViewAllCV;
