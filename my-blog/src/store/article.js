import { defineStore } from 'pinia';

export const useArticleStore = defineStore('article', {
  state: () => ({
    articles: [
      {
        id: '1',
        title: '原型、原型链与继承',
        category: 'fullstack',
        subcategory: 'js',
        date: '2023-01-15',
        summary: '深入理解JavaScript中的原型、原型链与继承机制',
        content: `
# 原型、原型链与继承

JavaScript 是一种基于原型的语言，这意味着对象可以从其他对象继承属性和方法。这种机制与传统的基于类的继承不同。

## 原型（Prototype）

每个 JavaScript 对象都有一个原型（prototype）。原型本身也是一个对象，因此原型也有自己的原型，形成了一个原型链。

\`\`\`javascript
// 构造函数
function Person(name) {
  this.name = name;
}

// 在原型上添加方法
Person.prototype.sayHello = function() {
  return \`Hello, my name is \${this.name}\`;
};

// 创建实例
const alice = new Person('Alice');
console.log(alice.sayHello()); // "Hello, my name is Alice"
\`\`\`

## 原型链（Prototype Chain）

当我们尝试访问一个对象的属性时，JavaScript 会首先在对象本身查找该属性。如果没有找到，它会继续在对象的原型中查找，然后是原型的原型，以此类推，直到找到该属性或到达原型链的末端（通常是 Object.prototype）。

\`\`\`javascript
// alice 实例没有 toString 方法，但可以通过原型链调用 Object.prototype.toString
console.log(alice.toString()); // "[object Object]"
\`\`\`

## 继承（Inheritance）

在 JavaScript 中，我们可以通过原型链实现继承：

\`\`\`javascript
// 父类构造函数
function Animal(name) {
  this.name = name;
}

Animal.prototype.eat = function() {
  return \`\${this.name} is eating.\`;
};

// 子类构造函数
function Dog(name, breed) {
  Animal.call(this, name); // 调用父类构造函数
  this.breed = breed;
}

// 设置原型链，使 Dog 继承 Animal
Dog.prototype = Object.create(Animal.prototype);
Dog.prototype.constructor = Dog;

// 添加子类特有的方法
Dog.prototype.bark = function() {
  return 'Woof!';
};

// 创建实例
const max = new Dog('Max', 'Labrador');
console.log(max.eat()); // "Max is eating."
console.log(max.bark()); // "Woof!"
\`\`\`

这种继承模式被称为"原型继承"，是 JavaScript 中实现代码重用的基础机制。
        `,
      },
      {
        id: '2',
        title: '作用域与闭包',
        category: 'fullstack',
        subcategory: 'js',
        date: '2023-02-10',
        summary: '理解JavaScript中的作用域、作用域链和闭包概念',
        content: `
# 作用域与闭包

## 作用域（Scope）

作用域是指程序中定义变量的区域，它决定了变量的可访问性（可见性）。JavaScript 主要有以下几种作用域：

1. **全局作用域**：变量在函数外部定义，可以在整个程序中访问。
2. **函数作用域**：变量在函数内部定义，只能在函数内部访问。
3. **块级作用域**：通过 let 和 const 关键字定义的变量，只能在定义它们的块（由 {} 包围）内访问。

\`\`\`javascript
// 全局作用域
const globalVar = 'I am global';

function exampleFunction() {
  // 函数作用域
  const functionVar = 'I am function-scoped';
  
  if (true) {
    // 块级作用域
    let blockVar = 'I am block-scoped';
    console.log(globalVar);      // 可访问
    console.log(functionVar);    // 可访问
    console.log(blockVar);       // 可访问
  }
  
  console.log(globalVar);        // 可访问
  console.log(functionVar);      // 可访问
  // console.log(blockVar);      // 错误：blockVar 在这里不可访问
}

console.log(globalVar);          // 可访问
// console.log(functionVar);     // 错误：functionVar 在这里不可访问
\`\`\`

## 作用域链（Scope Chain）

当代码尝试访问一个变量时，JavaScript 引擎会首先在当前作用域中查找。如果没有找到，它会继续在外部作用域中查找，直到找到该变量或到达全局作用域。这一系列的作用域查找形成了作用域链。

\`\`\`javascript
const global = 'global';

function outer() {
  const outerVar = 'outer';
  
  function inner() {
    const innerVar = 'inner';
    console.log(innerVar);  // 当前作用域
    console.log(outerVar);  // 外部作用域
    console.log(global);    // 全局作用域
  }
  
  inner();
}

outer();
\`\`\`

## 闭包（Closure）

闭包是指一个函数可以记住并访问其词法作用域，即使该函数在其词法作用域之外执行。简单来说，闭包使得函数可以保留对创建它的作用域的访问权限。

\`\`\`javascript
function createCounter() {
  let count = 0;  // 私有变量
  
  return function() {
    count++;
    return count;
  };
}

const counter = createCounter();
console.log(counter());  // 1
console.log(counter());  // 2
console.log(counter());  // 3
\`\`\`

在这个例子中，返回的函数形成了一个闭包，它"记住"了 createCounter 函数作用域中的 count 变量，即使 createCounter 函数已经执行完毕。

闭包的主要用途：

1. **数据私有化**：创建私有变量和方法
2. **函数工厂**：创建具有特定行为的函数
3. **模块模式**：组织和封装代码

闭包是 JavaScript 中非常强大的特性，但也需要谨慎使用，因为它们可能导致内存泄漏（如果不再需要的闭包仍然被引用）。
        `,
      },
      {
        id: '3',
        title: '前端工程化实践',
        category: 'fullstack',
        subcategory: 'engineering',
        date: '2023-03-20',
        summary: '现代前端工程化实践与最佳方案',
        content: `
# 前端工程化实践

前端工程化是指将前端开发流程规范化、标准化，使用工程化方法、工具链和框架来提高开发效率和代码质量。

## 为什么需要前端工程化？

随着前端技术的快速发展，项目规模和复杂度不断增加，前端开发面临以下挑战：

1. **代码组织问题**：大型项目的代码量庞大，需要合理的模块化方案
2. **效率问题**：手动处理资源、依赖、构建过程耗时且容易出错
3. **性能问题**：需要代码压缩、分割、懒加载等优化手段
4. **协作问题**：多人开发需要统一的代码风格和提交规范
5. **质量问题**：需要自动化测试和持续集成保障代码质量

## 前端工程化的核心内容

### 1. 模块化开发

模块化是将复杂系统分解为独立、可复用的模块。现代前端主要采用 ES Modules、CommonJS 等模块化规范。

\`\`\`javascript
// math.js
export function add(a, b) {
  return a + b;
}

// app.js
import { add } from './math.js';
console.log(add(1, 2)); // 3
\`\`\`

### 2. 包管理工具

npm、Yarn、pnpm 等工具用于管理项目依赖，简化安装、更新和删除第三方库的过程。

\`\`\`json
{
  "name": "my-project",
  "version": "1.0.0",
  "dependencies": {
    "vue": "^3.2.0",
    "axios": "^0.24.0"
  },
  "devDependencies": {
    "vite": "^2.7.0",
    "eslint": "^8.5.0"
  }
}
\`\`\`

### 3. 构建工具

Webpack、Vite、Rollup 等构建工具用于将源代码转换为可部署的静态资源。

\`\`\`javascript
// vite.config.js
export default {
  plugins: [...],
  build: {
    outDir: 'dist',
    minify: 'terser',
    rollupOptions: {
      output: {
        manualChunks: {
          vendor: ['vue', 'vue-router']
        }
      }
    }
  }
}
\`\`\`

### 4. 代码规范和质量控制

ESLint、Prettier、StyleLint 等工具用于统一代码风格和发现潜在问题。

\`\`\`json
// .eslintrc.json
{
  "extends": ["eslint:recommended", "plugin:vue/vue3-recommended"],
  "rules": {
    "semi": ["error", "always"],
    "quotes": ["error", "single"]
  }
}
\`\`\`

### 5. 自动化测试

Jest、Vitest、Cypress 等测试框架用于单元测试、集成测试和端到端测试。

\`\`\`javascript
// sum.test.js
import { sum } from './sum';

test('adds 1 + 2 to equal 3', () => {
  expect(sum(1, 2)).toBe(3);
});
\`\`\`

### 6. CI/CD 流程

使用 GitHub Actions、Jenkins 等工具实现持续集成和持续部署。

\`\`\`yaml
# .github/workflows/deploy.yml
name: Deploy
on:
  push:
    branches: [main]
jobs:
  build-and-deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Install dependencies
        run: npm install
      - name: Build
        run: npm run build
      - name: Deploy
        uses: peaceiris/actions-gh-pages@v3
        with:
          github_token: "\${{ secrets.GITHUB_TOKEN }}"
          publish_dir: ./dist
\`\`\`

## 工程化最佳实践

1. **组件化开发**：将 UI 拆分为可复用的组件
2. **状态管理**：使用 Vuex/Pinia、Redux 等工具管理应用状态
3. **类型检查**：使用 TypeScript 增强代码的可靠性
4. **代码分割**：实现按需加载，提高首屏加载速度
5. **性能优化**：图片懒加载、资源预加载、缓存策略等
6. **版本控制**：使用语义化版本控制
7. **文档生成**：自动生成 API 文档和组件文档

前端工程化是一个持续演进的过程，需要根据项目规模和团队情况选择合适的工具和方法。良好的工程化实践可以显著提高开发效率和产品质量。
        `,
      },
    ],
    categories: [
      { id: 'fullstack', name: '全栈', icon: 'layers' },
      { id: 'article', name: '文章', icon: 'file-text' },
    ],
  }),
  getters: {
    getArticleById: (state) => (id) => {
      return state.articles.find(article => article.id === id);
    },
    getArticlesByCategory: (state) => (category) => {
      return state.articles.filter(article => article.category === category);
    },
    getAllArticles: (state) => {
      return state.articles;
    },
    getCategories: (state) => {
      return state.categories;
    }
  }
});
