import { useEffect, useState } from "react";


function ViewAllEmployees() {
  const [message, setMessage] = useState('');

  useEffect(() => {
     fetch('https://localhost:8081/api/v1/auth/message')
     .then(response => response.json())
     .then(data => {
        setMessage(data);
     })
     .catch(error => console.log(error));
  }, []);

  return (
    <div>
      <h1>Fetch Example</h1>
      {message ? <p>{message}</p> : <p>Loading...</p>}
    </div>
  );
}

export default ViewAllEmployees;
