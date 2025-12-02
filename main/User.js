export class User {
  constructor(username, password) {
    this.username = username;
    this.password = password;
  }

  getUsername() {
    return this.username;
  }

  matchPassword(p) {
    return this.password === p;
  }

  equals(obj) {
    if (obj === this) return true;
    if (!(obj instanceof User)) return false;
    return obj.getUsername() === this.username;
  }
}
