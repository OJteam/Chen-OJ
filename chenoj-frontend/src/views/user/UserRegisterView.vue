<template>
  <div class="userLoginView">
    <h2 style="margin-bottom: 16px">用户注册</h2>
    <a-form
      :model="form"
      @submit="handleSubmit"
      label-align="left"
      auto-label-width
      style="max-width: 480px; margin: 0 auto"
    >
      <a-form-item
        :rules="[
          { required: true, message: '账号不能为空' },
          { minLength: 4, message: '账号长度不能低于四位' },
        ]"
        field="userAccount"
        label="账号 :"
      >
        <a-input v-model="form.userAccount" placeholder="请输入账号" />
      </a-form-item>
      <a-form-item
        :rules="[
          { required: true, message: '密码不能为空' },
          { minLength: 6, message: '密码长度不能低于八位' },
        ]"
        field="userPassword"
        tooltip="密码不少于8位"
        label="密码 :"
      >
        <a-input-password
          v-model="form.userPassword"
          placeholder="请输入密码"
        />
      </a-form-item>
      <a-form-item
        :rules="[
          { required: true, message: '密码不能为空' },
          { minLength: 6, message: '密码长度不能低于八位' },
        ]"
        field="checkPassword"
        tooltip="密码不少于8位"
        label="重复密码 :"
      >
        <a-input-password
          v-model="form.checkPassword"
          placeholder="请再次输入密码"
        />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 120px">
          注册
        </a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script lang="ts" setup>
import { reactive } from "vue";
import {
  User,
  UserControllerService,
  UserRegisterRequest,
} from "../../../generated";
import { Message } from "@arco-design/web-vue";
import store from "@/store";
import { useRouter } from "vue-router";

const router = useRouter();

const form = reactive<UserRegisterRequest>({
  userAccount: "",
  userPassword: "",
  checkPassword: "",
});

const handleSubmit = async () => {
  if (
    !form.userAccount ||
    form.userAccount.length < 4 ||
    !form.userPassword ||
    form.userPassword.length < 8
  ) {
    Message.error("账号或密码长度不符");
    return;
  }

  if (
    !form.checkPassword ||
    form.checkPassword.length !== form.userPassword.length ||
    form.checkPassword !== form.userPassword
  ) {
    Message.error("两次输入密码不一致");
    return;
  }
  // 检查账号是否已存在
  const accountExists = await checkUserAccountExists(form.userAccount);
  if (accountExists) {
    Message.error("该账号已被注册");
    return;
  }

  const res = await UserControllerService.userRegisterUsingPost(form);
  if (res.code === 0) {
    Message.success("注册成功！");
    await store.dispatch("getLoginUser");
    await router.push({
      path: "/user/login",
      replace: true,
    });
  } else {
    Message.error(res.msg);
  }
};
// 检查用户账号是否存在
const checkUserAccountExists = async (account: string): Promise<boolean> => {
  const res = await UserControllerService.listUserByPageWhenRegisterUsingPost({
    userAccount: account,
    current: 1, // 设置当前页
    pageSize: 10, // 设置每页大小
  });

  // 假设返回的数据结构为 { data: { records: User[], total: number } }
  const users = res.data?.records; // 获取用户记录
  return (
    Array.isArray(users) &&
    users.some((user: { userAccount: string }) => user.userAccount === account)
  );
};
</script>
