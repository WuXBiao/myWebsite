<template>
  <MainLayout>
    <div class="bg-gray-50 dark:bg-gray-900 py-12">
      <div class="container mx-auto px-4">
        <div class="max-w-5xl mx-auto">
          <div class="flex items-center justify-between mb-6">
            <h1 class="text-2xl md:text-3xl font-bold text-gray-900 dark:text-white">
              {{ isEdit ? '编辑文章' : '新增文章' }}
            </h1>
            <div class="flex items-center gap-3">
              <button
                type="button"
                class="px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-800 dark:text-gray-200 hover:bg-gray-100 dark:hover:bg-gray-700"
                @click="togglePreview"
              >
                {{ showPreview ? '编辑' : '预览' }}
              </button>
              <button
                type="button"
                class="px-4 py-2 rounded-lg bg-primary text-white hover:bg-blue-600"
                @click="onSave"
              >
                保存
              </button>
            </div>
          </div>

          <div v-if="error" class="mb-4 p-3 rounded bg-red-50 text-red-700 border border-red-200">
            {{ error }}
          </div>

          <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <div class="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6">
              <div class="space-y-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">标题</label>
                  <input
                    v-model="form.title"
                    type="text"
                    class="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-900 text-gray-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-primary"
                    placeholder="请输入标题"
                  />
                </div>

                <div>
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">摘要</label>
                  <textarea
                    v-model="form.summary"
                    rows="3"
                    class="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-900 text-gray-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-primary"
                    placeholder="请输入摘要"
                  ></textarea>
                </div>

                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">分类</label>
                    <select
                      v-model="form.category"
                      class="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-900 text-gray-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-primary"
                    >
                      <option v-for="cat in categories" :key="cat.id" :value="cat.id">
                        {{ cat.name }}
                      </option>
                    </select>
                  </div>

                  <div>
                    <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">子分类（可选）</label>
                    <input
                      v-model="form.subcategory"
                      type="text"
                      class="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-900 text-gray-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-primary"
                      placeholder="如：golang / vue / ..."
                    />
                  </div>
                </div>

                <div>
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">内容（Markdown）</label>
                  <textarea
                    v-model="form.content"
                    rows="16"
                    class="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-900 text-gray-900 dark:text-white font-mono text-sm focus:outline-none focus:ring-2 focus:ring-primary"
                    placeholder="# 标题\n\n在这里开始写你的文章..."
                  ></textarea>
                </div>
              </div>
            </div>

            <div class="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6">
              <div class="flex items-center justify-between mb-4">
                <h2 class="text-lg font-semibold text-gray-900 dark:text-white">预览</h2>
                <router-link
                  v-if="isEdit"
                  :to="`/article/${articleId}`"
                  class="text-primary dark:text-primary hover:underline text-sm"
                >
                  查看原文
                </router-link>
              </div>
              <div class="markdown-body prose dark:prose-invert max-w-none" v-html="previewHtml"></div>
            </div>
          </div>

          <div class="mt-6">
            <router-link
              to="/article"
              class="text-primary dark:text-primary hover:underline"
            >
              ← 返回文章列表
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { computed, reactive, ref, watchEffect } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import MainLayout from '../components/layout/MainLayout.vue';
import { useArticleStore } from '../store/article';
import { markdownToHtml, formatDate } from '../utils/format';

const route = useRoute();
const router = useRouter();
const articleStore = useArticleStore();

const showPreview = ref(true);
const error = ref('');

const articleId = computed(() => route.params.id);
const isEdit = computed(() => Boolean(articleId.value));
const categories = computed(() => articleStore.getCategories);

const form = reactive({
  id: '',
  title: '',
  summary: '',
  category: categories.value?.[0]?.id || 'article',
  subcategory: '',
  date: '',
  content: ''
});

watchEffect(() => {
  if (!isEdit.value) {
    form.id = '';
    form.title = '';
    form.summary = '';
    form.category = categories.value?.[0]?.id || 'article';
    form.subcategory = '';
    form.date = '';
    form.content = '';
    return;
  }

  const existing = articleStore.getArticleById(articleId.value);
  if (!existing) return;

  form.id = existing.id;
  form.title = existing.title || '';
  form.summary = existing.summary || '';
  form.category = existing.category || (categories.value?.[0]?.id || 'article');
  form.subcategory = existing.subcategory || '';
  form.date = existing.date || '';
  form.content = existing.content || '';
});

const previewHtml = computed(() => {
  if (!showPreview.value) return '';
  return markdownToHtml(form.content);
});

const togglePreview = () => {
  showPreview.value = !showPreview.value;
};

const genId = () => {
  return String(Date.now());
};

const nowDateStr = () => {
  return formatDate(new Date(), 'YYYY-MM-DD');
};

const onSave = () => {
  error.value = '';

  const title = form.title.trim();
  const summary = form.summary.trim();
  const category = form.category;
  const content = form.content;

  if (!title) {
    error.value = '标题不能为空';
    return;
  }
  if (!summary) {
    error.value = '摘要不能为空';
    return;
  }
  if (!category) {
    error.value = '请选择分类';
    return;
  }
  if (!content || !content.trim()) {
    error.value = '内容不能为空';
    return;
  }

  const id = isEdit.value ? String(articleId.value) : genId();
  const date = isEdit.value && form.date ? form.date : nowDateStr();

  articleStore.upsertArticle({
    id,
    title,
    summary,
    category,
    subcategory: form.subcategory.trim(),
    date,
    content
  });

  router.push(`/article/${id}`);
};
</script>
