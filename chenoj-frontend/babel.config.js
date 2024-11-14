module.exports = {
  presets: [
    "@vue/cli-plugin-babel/preset", // 使用 Vue CLI 的默认 Babel preset
  ],
  plugins: [
    "@babel/plugin-transform-class-static-block", // 添加静态类块的支持
  ],
};
