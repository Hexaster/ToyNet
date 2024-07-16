#include "TensorObj.h"
#include <vector>
#include <variant>
#include <any>
#include <type_traits>


template<typename T>
TensorObj<T>::TensorObj(const std::vector<T> data, const std::vector<int> shape) {
    this->data = data;
    this->shape = shape;
    setStride();
}

template<typename T>
void TensorObj<T>::setData(const std::vector<T> data) {

}

/**
     * Given a list of shape, return the list of stride.
     * First initialise stride as [1], then from the last item of shape,
     * for each item until the second one, do multiplication on the first item of stride,
     * and add it to the first place.
     * E.g.
     * shape = [3, 3, 3]
     * stride = [9, 3, 1]
     * If shape is one-dimensional, the stride is always [1].
     */
template<typename T>
void TensorObj<T>::setStride() {
    this->stride.clear();
    this->stride.push_back(1);
    for (int d = this->shape.size()-1; d>0; d--) {
        this->stride.insert(this->stride.begin(),this->stride[0]*this->shape[d]);
    }
}

/**
 * Set the number of blocks of the tensor.
 * Given the shape, the number of blocks of the i-th layer is the multiplication of first i-1 dimensions
 */
template<typename T>
void TensorObj<T>::setBlocks() {
    this->blocks.clear();
    this->stride.push_back(1);
    for (int d = 0; d < this->shape.size(); d++) {
        this->blocks.push_back(this->blocks[-1]*this->shape[d]);
    }
}




