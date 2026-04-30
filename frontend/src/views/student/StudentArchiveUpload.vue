<template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <h2 class="page-title-text">档案材料上传</h2>
        <p class="page-desc">上传档案材料文件，提交后由教师审核，审核通过后将存储到云端</p>
      </div>
      <button class="btn btn-primary" @click="openUploadModal">上传材料</button>
    </div>

    <!-- 我的档案文件列表 -->
    <div class="card">
      <div class="table-container">
        <table class="data-table">
          <thead>
            <tr>
              <th>文件名</th>
              <th>材料分类</th>
              <th>文件大小</th>
              <th>描述</th>
              <th>状态</th>
              <th>上传时间</th>
              <th>审核意见</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in fileList" :key="item.pkArchiveFile">
              <td class="text-small-medium" style="color: var(--text-primary)">{{ item.fileName }}</td>
              <td>{{ item.categoryName || '-' }}</td>
              <td class="text-caption">{{ formatSize(item.fileSize) }}</td>
              <td class="text-caption">{{ item.description || '-' }}</td>
              <td>
                <span :class="['badge', statusBadge(item.status)]">{{ statusLabel(item.status) }}</span>
              </td>
              <td class="text-caption">{{ formatDate(item.uploadTime) }}</td>
              <td class="text-caption">{{ item.auditRemark || '-' }}</td>
              <td>
                <div class="flex gap-4">
                  <a v-if="item.status === 1 && item.filePath && item.filePath.startsWith('http')"
                     :href="item.filePath" target="_blank" class="btn btn-subtle btn-sm">查看</a>
                  <button v-if="item.status === 0" class="btn btn-danger btn-sm" @click="handleDelete(item)">撤回</button>
                </div>
              </td>
            </tr>
            <tr v-if="fileList.length === 0">
              <td colspan="8" class="empty-cell">暂无档案材料，点击右上角"上传材料"开始</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 上传弹窗 -->
    <div class="modal-overlay" v-if="showUploadModal" @click.self="showUploadModal = false">
      <div class="modal-content" style="max-width: 520px">
        <div class="modal-header">
          <h3 class="modal-title">上传档案材料</h3>
          <button class="btn-icon" @click="showUploadModal = false">✕</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">材料分类 *</label>
            <select class="form-input" v-model="uploadForm.categoryId" required>
              <option value="">请选择分类</option>
              <option v-for="c in categories" :key="c.pkCategory" :value="c.pkCategory">{{ c.categoryName }}</option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-label">选择文件 *</label>
            <div class="file-upload-area" @click="triggerFileInput" @dragover.prevent @drop.prevent="handleDrop">
              <input ref="fileInputRef" type="file" style="display:none" @change="handleFileSelect"
                     accept=".pdf,.doc,.docx,.jpg,.jpeg,.png,.zip,.rar" />
              <div v-if="!selectedFile" class="upload-placeholder">
                <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="var(--text-quaternary)" stroke-width="1.5">
                  <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="17 8 12 3 7 8"/><line x1="12" y1="3" x2="12" y2="15"/>
                </svg>
                <p class="text-caption">点击或拖拽文件到此区域</p>
                <p class="text-caption" style="color: var(--text-quaternary); font-size: 12px">支持 PDF、Word、图片、压缩包，最大 50MB</p>
              </div>
              <div v-else class="upload-selected">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="var(--status-emerald)" stroke-width="2">
                  <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/>
                </svg>
                <div class="file-info">
                  <span class="text-small-medium" style="color: var(--text-primary)">{{ selectedFile.name }}</span>
                  <span class="text-caption">{{ formatSize(selectedFile.size) }}</span>
                </div>
                <button class="btn-icon" @click.stop="clearFile" style="margin-left: auto">✕</button>
              </div>
            </div>
          </div>
          <div class="form-group">
            <label class="form-label">描述（选填）</label>
            <textarea class="form-input" v-model="uploadForm.description" rows="2" placeholder="简要描述此材料内容"></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-ghost btn-sm" @click="showUploadModal = false">取消</button>
          <button class="btn btn-primary btn-sm" @click="handleUpload" :disabled="uploading">
            {{ uploading ? '上传中...' : '提交审核' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useUserStore } from '../../stores/user'
import { archiveApi, studentApi } from '../../api/modules'

const userStore = useUserStore()
const fileList = ref<any[]>([])
const categories = ref<any[]>([])
const studentInfo = ref<any>(null)
const showUploadModal = ref(false)
const uploading = ref(false)
const selectedFile = ref<File | null>(null)
const fileInputRef = ref<HTMLInputElement | null>(null)
const uploadForm = ref({ categoryId: '', description: '' })

onMounted(async () => {
  await loadStudentInfo()
  await Promise.all([loadCategories(), loadFiles()])
})

async function loadStudentInfo() {
  try {
    const userId = userStore.userInfo?.userId
    if (!userId) return
    const res: any = await studentApi.getByUserId(userId)
    studentInfo.value = res.data
  } catch (e) { console.error(e) }
}

async function loadCategories() {
  try {
    const res: any = await archiveApi.getCategoryList()
    categories.value = res.data || []
  } catch (e) { console.error(e) }
}

async function loadFiles() {
  if (!studentInfo.value?.pkStudent) return
  try {
    const res: any = await archiveApi.getFilesByStudentId(studentInfo.value.pkStudent)
    fileList.value = res.data || []
  } catch (e) { console.error(e) }
}

function openUploadModal() {
  uploadForm.value = { categoryId: '', description: '' }
  selectedFile.value = null
  showUploadModal.value = true
}

function triggerFileInput() {
  fileInputRef.value?.click()
}

function handleFileSelect(e: Event) {
  const input = e.target as HTMLInputElement
  if (input.files && input.files.length > 0) {
    selectedFile.value = input.files[0]
  }
}

function handleDrop(e: DragEvent) {
  if (e.dataTransfer?.files && e.dataTransfer.files.length > 0) {
    selectedFile.value = e.dataTransfer.files[0]
  }
}

function clearFile() {
  selectedFile.value = null
  if (fileInputRef.value) fileInputRef.value.value = ''
}

async function handleUpload() {
  if (!uploadForm.value.categoryId) { alert('请选择材料分类'); return }
  if (!selectedFile.value) { alert('请选择文件'); return }
  if (!studentInfo.value?.pkStudent) { alert('未找到学生信息'); return }

  uploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', selectedFile.value)
    formData.append('studentId', studentInfo.value.pkStudent.toString())
    formData.append('categoryId', uploadForm.value.categoryId)
    formData.append('uploadUserId', (userStore.userInfo?.userId || '').toString())
    if (uploadForm.value.description) {
      formData.append('description', uploadForm.value.description)
    }

    await archiveApi.uploadFile(formData)
    showUploadModal.value = false
    await loadFiles()
  } catch (e: any) {
    alert(e.message || '上传失败')
  } finally {
    uploading.value = false
  }
}

async function handleDelete(item: any) {
  if (!confirm(`确认撤回「${item.fileName}」？`)) return
  try {
    await archiveApi.deleteFile(item.pkArchiveFile)
    await loadFiles()
  } catch (e: any) {
    alert(e.response?.data?.message || '撤回失败')
  }
}

function statusBadge(s: number) {
  return ['badge-warning', 'badge-success', 'badge-danger'][s] || 'badge-neutral'
}
function statusLabel(s: number) {
  return ['待审核', '已通过', '已驳回'][s] || '未知'
}
function formatSize(bytes: number) {
  if (!bytes) return '-'
  if (bytes < 1024) return bytes + 'B'
  if (bytes < 1048576) return (bytes / 1024).toFixed(1) + 'KB'
  return (bytes / 1048576).toFixed(1) + 'MB'
}
function formatDate(dateStr: string) {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' })
}
</script>

<style scoped>
.page-container { display: flex; flex-direction: column; gap: 16px; }
.page-header { display: flex; justify-content: space-between; align-items: flex-start; padding: 8px 0; }
.page-title-text { font-size: 20px; font-weight: 590; color: var(--text-primary); letter-spacing: -0.24px; }
.page-desc { font-size: 14px; color: var(--text-tertiary); margin-top: 4px; }
.empty-cell { text-align: center; padding: 40px !important; color: var(--text-quaternary); font-size: 13px; }

.file-upload-area {
  border: 1.5px dashed var(--border-secondary);
  border-radius: var(--radius-card);
  padding: 24px;
  cursor: pointer;
  transition: all 0.15s ease;
  background: var(--surface-02);
}
.file-upload-area:hover {
  border-color: var(--accent-violet);
  background: var(--surface-04);
}
.upload-placeholder {
  display: flex; flex-direction: column; align-items: center; gap: 8px;
}
.upload-selected {
  display: flex; align-items: center; gap: 12px;
}
.file-info {
  display: flex; flex-direction: column; gap: 2px; min-width: 0;
}
</style>
