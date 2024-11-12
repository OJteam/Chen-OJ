import fetch from 'node-fetch';
import { generate } from 'openapi-typescript-codegen';
import fs from 'fs';
import path from 'path';

// 定义 API 文档的 URL 和本地文件路径
const apiUrl = 'http://localhost:8121/api/v2/api-docs';
const localFilePath = path.join(process.cwd(), 'api-docs.json');
const outputDir = path.join(process.cwd(), 'generated');
const openAPIConfigFile = path.join(outputDir, 'core/OpenAPI.ts');

// 下载 API 文档并生成客户端代码
async function generateApiClient() {
  try {
    // 下载 API 文档
    const response = await fetch(apiUrl);
    if (!response.ok) {
      throw new Error(`Failed to fetch API docs: ${response.statusText}`);
    }
    const apiSpec = await response.json();

    // 将 API 文档保存到本地
    fs.writeFileSync(localFilePath, JSON.stringify(apiSpec, null, 2));

    // 使用 openapi-typescript-codegen 生成代码
    await generate({
      input: localFilePath,
      output: outputDir,
      client: 'axios',
    });

    // 修改生成的 OpenAPI.ts 文件中的 BASE URL
    let openAPIContent = fs.readFileSync(openAPIConfigFile, 'utf-8');
    openAPIContent = openAPIContent.replace(/BASE: 'http:\/\/localhost:8121\/api'/, "BASE: 'http://localhost:8121'");
    fs.writeFileSync(openAPIConfigFile, openAPIContent);

    console.log('API client generated and BASE URL updated successfully!');
  } catch (error) {
    console.error(`Error: ${error.message}`);
  }
}

generateApiClient();
