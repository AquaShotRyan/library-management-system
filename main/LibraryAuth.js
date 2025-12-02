import { AuthEnum } from "./AuthEnum.js";

export default class LibraryAuth {
  constructor(borrowers) {
    this.users = borrowers;
  }

  authUser(username, password) {
    if (!this.validateUsername(username) || !this.validatePassword(password)) {
      return AuthEnum.INVALID_INPUT;
    }

    let user;
    try {
      user = this.users.getBorrower(username);

      if (
        !user ||
        typeof user.matchPassword !== "function" ||
        !user.matchPassword(password)
      )
        return AuthEnum.INVALID_CREDENTIALS;
    } catch (e) {
      return AuthEnum.INVALID_CREDENTIALS;
    }

    return AuthEnum.SUCCESS;
  }

  validateUsername(u) {
    return typeof u === "string" && u.length > 0;
  }

  validatePassword(p) {
    return typeof p === "string" && p.length > 0;
  }
}
