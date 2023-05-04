<template>
  <div class="app-container">
    <!-- 顶部查询表单 -->
    <div class="search-div">
      <el-form label-width="70px" size="small">
        <el-row>
          <el-col :span="24">
            <el-form-item label="角色名称">
              <el-input style="width: 100%" v-model="searchObj.roleName" placeholder="角色名称"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row style="display:flex">
          <el-button type="primary" icon="el-icon-search" size="mini" :loading="loading" @click="fetchData()">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetData">重置</el-button>
        </el-row>
      </el-form>
    </div>
    <!-- “添加”按钮 -->
    <div class="tools-div">
        <el-button type="success" icon="el-icon-plus" size="mini" @click="add">添 加</el-button>
    </div>
    <!-- 表格渲染 -->
    <el-table
      v-loading="listLoading"
      :data="list"
      stripe
      border
      style="width: 100%;margin-top: 10px;"
      @selection-change="handleSelectionChange">

      <el-table-column type="selection"/>

      <el-table-column
        label="序号"
        width="70"
        align="center">
        <template slot-scope="scope">
          {{ (page - 1) * limit + scope.$index + 1 }}
        </template>
      </el-table-column>

      <el-table-column prop="roleName" label="角色名称" />
      <el-table-column prop="roleCode" label="角色编码" />
      <!-- <el-table-column prop="createTime" label="创建时间" width="160"/> -->
      <el-table-column label="操作" width="200" align="center">
        <template slot-scope="scope">
          <el-button type="primary" icon="el-icon-edit" size="mini" @click="edit(scope.row.id)" title="修改"/>
          <el-button type="danger" icon="el-icon-delete" size="mini" @click="removeDataById(scope.row.id)" title="删除"/>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <el-pagination
        :current-page="page"
        :total="total"
        :page-size="limit"
        style="padding: 30px 0; text-align: center;"
        layout="total, prev, pager, next, jumper"
        @current-change="fetchData"
    />
    <!-- 弹出层 -->
    <el-dialog title="添加/修改" :visible.sync="dialogVisible" width="40%" >
      <el-form ref="dataForm" :model="sysRole" label-width="150px" size="small" style="padding-right: 40px;">
        <el-form-item label="角色名称">
          <el-input v-model="sysRole.roleName"/>
        </el-form-item>
        <el-form-item label="角色编码">
          <el-input v-model="sysRole.roleCode"/>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false" size="small" icon="el-icon-refresh-right">取 消</el-button>
        <el-button type="primary" icon="el-icon-check" @click="saveOrUpdate()" size="small">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import api from '@/api/system/sysRole'
export default {
  // 定义数据模型(变量的初始值)
  data() {
    return {
        list: [], // 列表
        total: 0, // 总记录数
        page: 1, // 页码
        limit: 10, // 每页记录数
        searchObj: {}, // 查询条件
        // multipleSelection: []// 批量删除选中的记录列表

        dialogVisible:false, //是否弹窗
        sysRole:{} //封装表单角色数据
    }
  },
  // 页面渲染之前执行
  created() {
    this.fetchData()
  },
  // 定义方法
  methods: {
    fetchData(current=1) {
      this.page = current
      // 调用api
      api.getPageList(this.page, this.limit, this.searchObj).then(response => {
        this.list = response.data.records
        this.total = response.data.total
      })
    },
    // 根据id删除数据
    removeDataById(id) {
        this.$confirm('此操作将永久删除该记录, 是否继续?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }).then(() => { // promise
            // 点击确定，远程调用ajax
            return api.removeById(id)
            //调用返回的结果进入下一个then
        }).then((response) => {
            this.fetchData(this.page)
            this.$message.success(response.message || '删除成功')
        })
    },

    //点击按钮后弹出对话框
    add(){
        this.dialogVisible = true
    },

    //添加或修改
    saveOrUpdate(){
        if(!this.sysRole.id){
            this.save()
        }else{
            this.update()
        }
    },

    //添加角色
    save(){
        api.saveRole(this.sysRole).then(response => {
            this.$message.success(response.message || '操作成功') //提示
            this.dialogVisible=false //关闭弹窗
            this.fetchData() //刷新页面
        })
    },

    //点击“修改”弹出窗口，回显数据
    edit(id){
      this.dialogVisible=true
      this.fetchDataById(id)
    },

    //根据id查询
    fetchDataById(id){
      api.getById(id).then(response => {
        this.sysRole=response.data
      })
    },

    //修改角色
    update(){
      api.updateById(this.sysRole).then(response => {
        this.$message.success(response.message || '操作成功') //提示
        this.dialogVisible=false //关闭弹窗
        this.fetchData() //刷新页面
      })
    }
    
  }
}
</script>