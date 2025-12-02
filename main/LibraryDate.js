export default class LibraryDate {
  constructor(...args) {
    // Possible signatures:
    // new LibraryDate(year, month, day)
    // new LibraryDate(libraryDate)
    // new LibraryDate(date) where date is a JS Date
    if (args.length === 3 && typeof args[0] === "number") {
      const [year, month, day] = args;
      this.date = new Date(year, month, day);
    } else if (args.length === 1) {
      const arg = args[0];
      if (arg instanceof LibraryDate) {
        this.date = new Date(arg.getDate().getTime());
      } else if (arg instanceof Date) {
        this.date = new Date(arg.getTime());
      } else {
        // try to construct from an object with year/month/day
        const { year, month, day } = arg || {};
        if (
          typeof year === "number" &&
          typeof month === "number" &&
          typeof day === "number"
        ) {
          this.date = new Date(year, month, day);
        } else {
          this.date = new Date();
        }
      }
    } else {
      // default to current date
      this.date = new Date();
    }
  }

  addDays(days) {
    this.date.setDate(this.date.getDate() + days);
  }

  getDate() {
    return new Date(this.date.getTime());
  }

  toString() {
    const year = this.date.getFullYear();
    const month = this.date.getMonth() + 1; // JS months are 0-based
    const day = this.date.getDate();

    const monthStr = month <= 9 ? `0${month}` : `${month}`;
    const dayStr = day <= 9 ? `0${day}` : `${day}`;

    return `${year}-${monthStr}-${dayStr}`;
  }

  equals(obj) {
    if (obj === this) return true;
    if (!(obj instanceof LibraryDate)) return false;
    const calendar = obj.getDate();

    const equalYear = calendar.getFullYear() === this.date.getFullYear();
    const equalMonth = calendar.getMonth() === this.date.getMonth();
    const equalDay = calendar.getDate() === this.date.getDate();

    return equalYear && equalMonth && equalDay;
  }
}
