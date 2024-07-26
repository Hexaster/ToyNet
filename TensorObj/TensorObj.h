#ifndef TENSOROBJ_LIBRARY_H
#define TENSOROBJ_LIBRARY_H

#include <vector>
#include <type_traits>

template<typename T>
class TensorObj{
    // Make sure that arrays only store numbers.
    static_assert(std::is_arithmetic_v<T>, "Array can only store numeric types.");

    std::vector<T> data;
    std::vector<int> shape;
    std::vector<int> stride;
    std::vector<int> blocks;

public:
    TensorObj(std::vector<T> data, std::vector<int> shape);
private:
    // Setters
    void setData(std::vector<T> data);
    void setStrideC();
    void setBlocksC();
};


#endif //TENSOROBJ_LIBRARY_H
