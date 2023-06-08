import UnauthorizedAccess from "../pages/UnauthorizedAccess";
import { UseLoggedUser } from "../hooks/UseLoggedUserInformation";


interface ProtectedProps {
  children: React.ReactNode;
  role: string;
}

/*async function fetchLoggedUser() {
  const response = await fetch('http://localhost:8082/getLoggedUser', {
    credentials: "include",
  });
  const user = await response.json();
  return user;
}*/

const Protected: React.FC<ProtectedProps> = ({ children, role }) => {
  //const [loggedUser, setLoggedUser] = useState<User | null>(null);

  /*useEffect(() => {
    fetchLoggedUser().then(user => {
      setLoggedUser(user); 
    });
  }, []);*/

  const loggedUser = UseLoggedUser()

  if (loggedUser && loggedUser.role.name.toString() === role) {
    return <>{children}</>;
  } else {
    return <UnauthorizedAccess />;
  }
};

export default Protected;