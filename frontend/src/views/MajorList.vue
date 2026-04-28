<template>
  <div class="page">
    <div class="page-toolbar">
      <input v-model="keyword" class="form-input toolbar-input" placeholder="搜索专业名称" />
      <button class="btn btn-primary btn-sm" @click="showModal = true; isEdit = false; form = { majorName: '', majorCode: '', collegeId: null }">新增专业</button>
    </div>
    <div class="table-container">
      <table class="data-table">
        <thead><tr><th>专业名称</th><th>专业代码</th><th>所属学院</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="item in list" :key="item.pkMajor">
            <td class="text-small-medium" style="color:var(--text-primary)">{{ item.majorName }}</td>
            <td>{{ item.majorCode || '-' }}</td>
            <td>{{ item.collegeName }}</td>
            <td>
              <div class="flex gap-4">
                <button class="btn btn-subtle btn-sm" @click="isEdit = true; form = { ...item }; showModal = true">编辑</button>
                <button class="btn btn-danger btn-sm" @click="list = list.filter(i => i !== item)">删除</button>
              </div>
            </td>
          </tr>
          <tr v-if="!list.length"><td colspan="4" class="empty-cell">暂无数据</td></tr>
        </tbody>
      </table>
    </div>
    <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
      <div class="modal-content">
        <div class="modal-header"><h3 class="modal-title">{{ isEdit ? '编辑' : '新增' }}专业</h3><button class="btn-icon" @click="showModal = false">✕</button></div>
        <div class="modal-body">
          <div class="form-group"><label class="form-label">专业名称</label><input v-model="form.majorName" class="form-input" placeholder="请输入专业名称" /></div>
          <div class="form-group"><label class="form-label">专业代码</label><input v-model="form.majorCode" class="form-input" placeholder="请输入专业代码" /></div>
          <div class="form-group"><label class="form-label">所属学院</label>
            <select v-model="form.collegeId" class="form-select">
              <option :value="null">请选择学院</option>
              <option :value="1">计算机学院</option>
              <option :value="2">文学院</option>
              <option :value="3">理学院</option>
            </select>
          </div>
        </div>
        <div class="modal-footer"><button class="btn btn-ghost btn-sm" @click="showModal = false">取消</button><button class="btn btn-primary btn-sm" @click="showModal = false">保存</button></div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted } from 'vue'
const list = ref<any[]>([])
const keyword = ref('')
const showModal = ref(false)
const isEdit = ref(false)
const form = ref<any>({})
onMounted(() => {
  list.value = [
    { pkMajor: 1, majorName: '软件工程', majorCode: 'SE', collegeName: '计算机学院', collegeId: 1 },
    { pkMajor: 2, majorName: '计算机科学', majorCode: 'CS', collegeName: '计算机学院', collegeId: 1 },
    { pkMajor: 3, majorName: '汉语言文学', majorCode: 'CL', collegeName: '文学院', collegeId: 2 },
    { pkMajor: 4, majorName: '数学', majorCode: 'MA', collegeName: '理学院', collegeId: 3 },
  ]
})
</script>
<style scoped>
.page { display: flex; flex-direction: column; gap: 16px; }
.page-toolbar { display: flex; justify-content: space-between; align-items: center; }
.toolbar-input { width: 240px; padding: 7px 12px; font-size: 13px; }
.empty-cell { text-align: center; padding: 40px !important; color: var(--text-quaternary); font-size: 13px; }
</style>
