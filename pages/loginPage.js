import { pageTemplate } from "./pageTemplate.js";

export function loginPage() {
  const title = "Library";
  const contents = `
    <div class="login-container">
      <label for="username">Username:</label>
      <input id="input-username" type="text" name="username" autocomplete="on" />

      <label for="password">Password:</label>
      <input id="input-password" type="password" name="password" autocomplete="on" />
    
      <div id="login-err" style="color:red"></div>
      <button class="btn-regular" id="btn-login" onclick="handleLoginBtn()">Login</button>
    </div>
  `;

  return pageTemplate(title, contents, "");
}
