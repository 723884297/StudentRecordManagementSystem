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
                  <button class="btn btn-subtle btn-sm" @click="openPreview(item)">预览</button>
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

    <!-- 文件预览弹窗 -->
    <div v-if="showPreviewModal" class="modal-overlay" @click.self="closePreview">
      <div class="modal-content preview-modal">
        <div class="modal-header">
          <h3 class="modal-title">档案预览 · {{ previewItem?.fileName }}</h3>
          <button class="btn-icon" @click="closePreview">✕</button>
        </div>
        <div class="modal-body preview-body">
          <div class="preview-meta">
            <span class="meta-item">分类：{{ previewItem?.categoryName || '-' }}</span>
            <span class="meta-item">大小：{{ formatSize(previewItem?.fileSize) }}</span>
            <span class="meta-item">状态：<span :class="['badge', statusBadge(previewItem?.status)]">{{ statusLabel(previewItem?.status) }}</span></span>
            <span class="meta-item" v-if="previewItem?.description">说明：{{ previewItem.description }}</span>
          </div>
          <div class="preview-viewer">
            <div v-if="previewLoading" class="preview-tip">加载中...</div>
            <div v-else-if="previewError" class="preview-tip preview-error">{{ previewError }}</div>
            <template v-else-if="previewKind === 'image'">
              <img :src="previewBlobUrl" :alt="previewItem?.fileName" class="preview-image" />
            </template>
            <template v-else-if="previewKind === 'pdf'">
              <iframe :src="previewBlobUrl" class="preview-iframe"></iframe>
            </template>
            <template v-else-if="previewKind === 'text'">
              <pre class="preview-text">{{ previewText }}</pre>
            </template>
            <template v-else>
              <div class="preview-tip">
                <p>该文件类型暂不支持在线预览（{{ previewExt || '未知' }}）</p>
                <button class="btn btn-primary btn-sm" @click="downloadPreview">下载查看</button>
              </div>
            </template>
          </div>
        </div>
        <div class="modal-footer">
          <button v-if="previewBlobUrl" class="btn btn-subtle btn-sm" @click="downloadPreview">下载</button>
          <button class="btn btn-ghost btn-sm" @click="closePreview">关闭</button>
        </div>
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

// 预览弹窗状态
const showPreviewModal = ref(false)
const previewItem = ref<any>(null)
const previewBlobUrl = ref('')
const previewKind = ref<'image' | 'pdf' | 'text' | 'other' | ''>('')
const previewExt = ref('')
const previewText = ref('')
const previewLoading = ref(false)
const previewError = ref('')

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

// 预览相关
function getExt(name: string): string {
  if (!name) return ''
  const idx = name.lastIndexOf('.')
  return idx >= 0 ? name.substring(idx + 1).toLowerCase() : ''
}

function kindOf(ext: string): 'image' | 'pdf' | 'text' | 'other' {
  if (['png', 'jpg', 'jpeg', 'gif', 'webp', 'bmp', 'svg'].includes(ext)) return 'image'
  if (ext === 'pdf') return 'pdf'
  if (['txt', 'log', 'md', 'json', 'csv', 'xml', 'html', 'htm'].includes(ext)) return 'text'
  return 'other'
}

async function openPreview(item: any) {
  previewItem.value = item
  showPreviewModal.value = true
  previewLoading.value = true
  previewError.value = ''
  previewBlobUrl.value = ''
  previewText.value = ''
  const ext = getExt(item.fileName)
  previewExt.value = ext
  previewKind.value = kindOf(ext)
  try {
    const res: any = await archiveApi.previewFile(item.pkArchiveFile)
    const blob: Blob = res.data
    previewBlobUrl.value = URL.createObjectURL(blob)
    if (previewKind.value === 'text') {
      previewText.value = await blob.text()
    }
  } catch (e: any) {
    previewError.value = e?.message || '预览加载失败'
  } finally {
    previewLoading.value = false
  }
}

function closePreview() {
  showPreviewModal.value = false
  if (previewBlobUrl.value) {
    URL.revokeObjectURL(previewBlobUrl.value)
  }
  previewBlobUrl.value = ''
  previewText.value = ''
  previewItem.value = null
  previewKind.value = ''
  previewExt.value = ''
  previewError.value = ''
}

function downloadPreview() {
  if (!previewBlobUrl.value || !previewItem.value) return
  const a = document.createElement('a')
  a.href = previewBlobUrl.value
  a.download = previewItem.value.fileName || 'archive-file'
  a.click()
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

/* 预览弹窗样式 */
.preview-modal { max-width: 880px; width: 90vw; }
.preview-body { display: flex; flex-direction: column; gap: 12px; }
.preview-meta { display: flex; flex-wrap: wrap; gap: 8px 16px; padding: 8px 12px; background: var(--surface-03); border-radius: var(--radius-md); font-size: 12px; color: var(--text-tertiary); }
.preview-meta .meta-item { display: inline-flex; align-items: center; gap: 4px; }
.preview-viewer { min-height: 360px; max-height: 65vh; display: flex; align-items: center; justify-content: center; background: var(--surface-02); border: 1px solid var(--border-subtle); border-radius: var(--radius-md); overflow: hidden; }
.preview-image { max-width: 100%; max-height: 65vh; object-fit: contain; }
.preview-iframe { width: 100%; height: 65vh; border: 0; background: #fff; }
.preview-text { width: 100%; max-height: 65vh; overflow: auto; padding: 16px; margin: 0; font-size: 12px; color: var(--text-secondary); white-space: pre-wrap; word-break: break-all; font-family: 'SF Mono', monospace; }
.preview-tip { display: flex; flex-direction: column; align-items: center; gap: 12px; padding: 32px; color: var(--text-tertiary); font-size: 13px; text-align: center; }
.preview-error { color: var(--status-red, #c53030); }
</style>
