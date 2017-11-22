# Solr-LTR-Training<br>
> ## Introduction
  >项目用于为Apache Solr（7.10）训练排序学习模型。项目基于特定格式的原始数据生成用于排序学习训练的数据集后，利用ranklib对数据集进行训练生成模型参数文件，并将ranklib的模型格式转换为solr的模型格式。
  目前支持的模型为[org.apache.solr.ltr.model.MultipleAdditiveTreesModel](https://lucene.apache.org/solr/7_0_0//solr-ltr/org/apache/solr/ltr/model/MultipleAdditiveTreesModel.html)。
  项目自带的原始数据来源于https://www.banggood.com 近一个星期内的搜索记录数据和近一个月内的商品特征数据。<br>
  
> ## Quick Start
  >下面将详细描述在现有数据[Solr-LTR-Training/OriginalData](https://github.com/AdienHuen/Solr-LTR-Training/tree/master/data/OriginalDataSet)的情况下，进行lambdaMART模型训练和solr-ltr配置的具体流程和操作。<br>