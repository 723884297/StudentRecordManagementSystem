<template>
  <div class="page">
    <div class="page-toolbar">
      <input v-model="keyword" class="form-input toolbar-input" placeholder="搜索班级名称" />
      <button class="btn btn-primary btn-sm" @click="showModal = true; isEdit = false; form = { className: '', classCode: '', majorId: null, grade: '' }">新增班级</button>
    </div>
    <div class="table-container">
      <table class="data-table">
        <thead><tr><th>班级名称</th><th>班级代码</th><th>所属专业</th><th>年级</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="item in filteredList" :key="item.pkClass">
            <td class="text-small-medium" style="color:var(--text-primary)">{{ item.className }}</td>
            <td>{{ item.classCode || '-' }}</td>
            <td>{{ item.majorName }}</td>
            <td>{{ item.grade }}</td>
            <td>
              <div class="flex gap-4">
                <button class="btn btn-subtle btn-sm" @click="isEdit = true; form = { ...item }; showModal = true">编辑</button>
                <button class="btn btn-danger btn-sm" @click="list = list.filter(i => i !== item)">删除</button>
              </div>
            </td>
          </tr>
          <tr v-if="!filteredList.length"><td colspan="5" class="empty-cell">暂无数据</td></tr>
        </tbody>
      </table>
    </div>
    <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
      <div class="modal-content">
        <div class="modal-header"><h3 class="modal-title">{{ isEdit ? '编辑' : '新增' }}班级</h3><button class="btn-icon" @click="showModal = false">✕</button></div>
        <div class="modal-body">
          <div class="form-group"><label class="form-label">班级名称</label><input v-model="form.className" class="form-input" placeholder="请输入班级名称" /></div>
          <div class="form-group"><label class="form-label">班级代码</label><input v-model="form.classCode" class="form-input" placeholder="请输入班级代码" /></div>
          <div class="form-group"><label class="form-label">所属专业</label>
            <select v-model="form.majorId" class="form-select">
              <option :value="null">请选择专业</option>
              <option :value="1">软件工程</option>
              <option :value="2">计算机科学</option>
              <option :value="3">汉语言文学</option>
            </select>
          </div>
          <div class="form-group"><label class="form-label">年级</label><input v-model="form.grade" class="form-input" placeholder="如：2024" /></div>
        </div>
        <div class="modal-footer"><button class="btn btn-ghost btn-sm" @click="showModal = false">取消</button><button class="btn btn-primary btn-sm" @click="showModal = false">保存</button></div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
const list = ref<any[]>([])
const keyword = ref('')
const showModal = ref(false)
const isEdit = ref(false)
const form = ref<any>({})
const filteredList = computed(() => {
  if (!keyword.value) return list.value
  return list.value.filter(i => i.className.includes(keyword.value))
})
onMounted(() => {
  list.value = [
    { pkClass: 1, className: '软工2401', classCode: 'SE2401', majorName: '软件工程', majorId: 1, grade: '2024' },
    { pkClass: 2, className: '软工2402', classCode: 'SE2402', majorName: '软件工程', majorId: 1, grade: '2024' },
    { pkClass: 3, className: '计科2401', classCode: 'CS2401', majorName: '计算机科学', majorId: 2, grade: '2024' },
    { pkClass: 4, className: '中文2301', classCode: 'CL2301', majorName: '汉语言文学', majorId: 3, grade: '2023' },
  ]
})
</script>
<style scoped>
.page { display: flex; flex-direction: column; gap: 16px; }
.page-toolbar { display: flex; justify-content: space-between; align-items: center; }
.toolbar-input { width: 240px; padding: 7px 12px; font-size: 13px; }
.empty-cell { text-align: center; padding: 40px !important; color: var(--text-quaternary); font-size: 13px; }
</style>
