import { User } from "../model/User";


export function useLoggedUser(): User | null {
    try {
      const loggedUser= localStorage.getItem('loggedUser');
      if (loggedUser) {
        console.log(loggedUser);
        
        return JSON.parse(loggedUser);
      } else {
        return null;
      }
    } catch (error) {
      console.error(error);
      return null;
    }
  }